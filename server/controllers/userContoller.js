const User = require("../models/User");
const PG = require("../models/pg");
const CustomError = require("../errors");
const { StatusCodes } = require("http-status-codes");
const { checkPermissions, attachCookieToResponse } = require("../utils");
const getAllUsers = async (req, res) => {
  const users = await User.find({ role: { $in: ["tenant", "owner"] } }).select(
    "-password"
  );
  res.status(StatusCodes.OK).json({ users });
};

const getSingleUser = async (req, res) => {
  const { id: userId } = req.params;
  const user = await User.findOne({ _id: userId }).select("-password");
  if (!user) {
    throw new CustomError.NotFoundError("user with given id does not exist");
  }
  // weather user is authorized to access other users
  checkPermissions(req.user, user._id);
  res.status(StatusCodes.OK).json({ user });
};

const showCurrentUser = async (req, res) => {
  res.status(StatusCodes.OK).json({ user: req.user });
};

const updateUser = async (req, res) => {
  const { name, email } = req.body;
  if (!name || !email) {
    throw new CustomError.BadRequestError("Please provide name and email");
  }
  const user = await User.find({ email });
  if (!user) {
    throw new CustomError.NotFoundError(
      "The user with the given email does not exist"
    );
  }
  // a user can not update other's user data
  checkPermissions(req.user, user._id);
  user.name = name;
  user.email = email;

  await user.save();
  const tokenUser = { name: user.name, userId: user._id, role: user.role };
  attachCookieToResponse({ res, user: tokenUser });
  res.status(StatusCodes.OK).json({ user: tokenUser });
};

const updatePassword = async (req, res) => {
  const { oldPassword, newPassword } = req.body;

  if (!oldPassword || !newPassword) {
    throw new CustomError.BadRequestError(
      "Please provide oldPassword as well as newPassword"
    );
  }

  const user = await User.findOne({ _id: req.user.userId });
  if (!user) {
    throw new CustomError.NotFoundError("Invalid Credentials");
  }
  user.password = newPassword;
  await user.save();
  res
    .status(StatusCodes.OK)
    .json({ msg: "Succcess !! Password updated successfully" });
};

const bookPG = async (req, res) => {
  const { pgId } = req.body;
  const userId = req.user.userId;

  const pg = await PG.findOne({ _id: pgId });
  if (!pg) {
    throw new CustomError.NotFoundError(
      "The PG with the given id is not found"
    );
  }
  const user = await User.findOne({ _id: userId });
  if (!user) {
    throw new CustomError.UnauthenticatedError("Invalid Credentials");
  }
  if (user.role !== "tenant") {
    throw new CustomError.BadRequestError(
      "You are not allowed to book a PG since you are not tenant"
    );
  }

  if (!pg.availability) {
    res.status(StatusCodes.OK).json({ msg: "The given PG is not available" });
  }

  // Check if the user has already booked this PG
  const alreadyBooked = user.bookedPGs.some(
    (booking) => booking.pgId.toString() === pgId
  );

  if (alreadyBooked) {
    return res.status(400).json({ message: "You have already booked this PG" });
  }

  user.bookedPGs.push({ pgId, status: "pending" });
  // pg.request = user._id;
  // pg.availability=false;
  await pg.save();
  await user.save();

  res.status(StatusCodes.OK).json({
    message: "Booking request submitted successfully",
    bookedPGs: user.bookedPGs,
  });
};

const handleBookingRequest = async (req, res) => {
  const { tenantId, pgId, action } = req.body;
  const ownerId = req.user.userId;

  const pg = await PG.findOne({ _id: pgId });
  if (!pg) {
    throw new CustomError.NotFoundError(
      `The PG with id: ${pgId} does not exist`
    );
  }
  if (pg.owner.toString() !== ownerId) {
    throw new CustomError.UnauthorizedError("This PG does not belong to you");
  }

  let tenant = await User.findOne({ _id: tenantId }).populate({
    path: "bookedPGs.pgId",
    model: "PG",
  });

  if (!tenant) {
    throw new CustomError.NotFoundError(
      `The tenant with id: ${tenantId} does not exist`
    );
  }

  // Find the booking request
  const booking = tenant.bookedPGs.find(
    (booking) => booking.pgId._id.toString() === pgId
  );

  if (!booking) {
    throw new CustomError.NotFoundError("Booking request not found");
  }

  if (action === "approved") {
    booking.status = "approved";
    pg.availability = false;
  } else if (action === "rejected") {
    booking.status = "rejected";
    pg.availability = true;
    tenant.bookedPGs = tenant.bookedPGs.filter(
      (booking) => booking.pgId._id.toString() !== pgId
    );
  } else {
    throw new CustomError.BadRequestError(
      "Invalid action. Use 'approved' or 'rejected'"
    );
  }

  await tenant.save();
  await pg.save();

  res.status(StatusCodes.OK).json({
    message: `Booking request ${action} successfully`,
    updatedBooking: booking,
  });
};

// const getPGs = async (req, res) => {
//   const userId = req.user.userId;
//   const user = await User.findOne({ _id: userId });
//   res.status(StatusCodes.OK).json({ bookedPGs: user.bookedPGs });
// };

const getPGs = async (req, res) => {
  try {
    const userId = req.user.userId;
    const user = await User.findOne({ _id: userId }).populate({
      path: "bookedPGs.pgId",
      model: "PG", // Explicitly reference the PG model
      populate: {
        path: "reviews",
        model: "Reviews", // Ensure correct model reference
      },
    });

    if (!user) {
      return res.status(StatusCodes.NOT_FOUND).json([]);
    }

    res.status(StatusCodes.OK).json(user.bookedPGs || []);
  } catch (error) {
    console.error(error);
    res.status(StatusCodes.INTERNAL_SERVER_ERROR).json([]);
  }
};

const deleteUser = async (req, res) => {
  const { userId } = req.body;
  if (!userId) {
    throw new CustomError.BadRequestError("Please provide the userId");
  }
  const user = await User.findOne({ _id: userId });
  if (!user) {
    throw new CustomError.BadRequestError(
      "The id of the given user does not exists"
    );
  }
  // a given user can not delete other users
  // checkPermissions(req.user,user._id)

  if (user.role === "owner") {
    await PG.deleteMany({ owner: userId });
  }

  if (user.role === "tenant") {
    await PG.updateMany(
      { _id: { $in: user.bookedPGs.map((booking) => booking.pgId) } },
      { $set: { availability: true } }
    );
  }
  // when the user is deleted all the reviews given by that user are also deleted use 'remove' hook
  await user.remove();
  res
    .status(StatusCodes.OK)
    .json({ msg: `user with id:${userId} deleted successfully`, user });
};

module.exports = {
  getAllUsers,
  getSingleUser,
  showCurrentUser,
  updateUser,
  updatePassword,
  bookPG,
  deleteUser,
  handleBookingRequest,
  getPGs,
};
