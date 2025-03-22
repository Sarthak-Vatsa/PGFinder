const CustomError = require("../errors");
const { StatusCodes } = require("http-status-codes");
const { isTokenValid } = require("../utils");

const authenticateUser = async (req, res, next) => {
  const token = req.signedCookies.token;

<<<<<<< HEAD
  if (!token) {
    throw new CustomError.UnauthenticatedError("Authentication Invalid");
  }

  try {
    const { name, userId, role } = isTokenValid({ token });
    req.user = { name, userId, role };
    next();
  } catch (error) {
    console.log(error);
  }
};

const authorizePermissions = (...roles) => {
  return (req, res, next) => {
    if (!roles.includes(req.user.role)) {
      throw new CustomError.UnautorizedError(
        "Authorization to this route failed"
      );
=======
    console.log(`Token received ${token}`)

    if(!token){
        throw new CustomError.UnauthenticatedError('Authentication Invalid')
>>>>>>> 9ea7b365e9a63ce0787606a7e6c76ed707c639c3
    }
    next();
  };
};

module.exports = {
  authenticateUser,
  authorizePermissions,
};
