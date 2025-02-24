const { StatusCodes } = require('http-status-codes')
const PG=require('../models/pg')
const User=require('../models/User')
const CustomError=require('../errors')

const getAllPGs = async (req, res) => {

    const { city, price } = req.query;
    const queryObject = {};
    //filtering
    
    if (city) {
        queryObject.city = { $regex: city, $options: "i" }; // Case insensitive search
    }

  
    let result = PG.find(queryObject);

   
    if (price === "highest") {
        result = result.sort("-price");
    } else if (price === "lowest") {
        result = result.sort("price");
    }

  
    const pgs = await result;

    res.status(StatusCodes.OK).json({ pgs });
};


const getSinglePG=async(req,res)=>{
    const {id:pgId}=req.params
    const pg = await PG.findOne({ _id: pgId })
    .populate({
        path: 'reviews',
        options: { sort: { createdAt: -1 } } // Sort reviews by latest
    });
    if(!pg){
        throw new CustomError.NotFoundError(`The PG with ${pgId} does not exists `)
    }
    res.status(StatusCodes.OK).json({pg});
}

const createPG=async(req,res)=>{
    const {name,address,city,price}=req.body
    if(!name || !address || !city || !price){
        throw new CustomError.BadRequestError('Please provide name,address,city,price')
    }
    if(price<=0){
        throw new CustomError.BadRequestError('Please provide the price>0')
    }
    const user=await User.findOne({_id:req.user.userId})

    if(user.role!=='owner'){
        throw new CustomError.BadRequestError('Only owners can create the PG')
    }

    const pg = await PG.create({
        name,
        city,
        address,
        price,
        availability:true, // Default to available
        owner:req.user.userId,
        user: req.user.userId
    });
    user.listings.push(pg._id);
    await user.save();

    res.status(StatusCodes.CREATED).json({ pg });
}

const updatePG=async(req,res)=>{
    const {id:pgId}=req.params
    const {name,city,address,price,availability}=req.body
    const pg=await PG.findOne({_id:pgId})
    if(!pg){
        throw new CustomError.NotFoundError(`The PG with ${pgId} does not exists `)
    }
    // one owner can not update pg of other owner
    const user=await User.findOne({_id:req.user.userId})
    if (user.role !== "owner" || !user.listings.some(pgItem => pgItem.toString() === pgId)) {
        throw new CustomError.UnautorizedError("You are not authorized to update this PG");
    }

    pg.name=name
    pg.city=city
    pg.address=address
    pg.price=price
    pg.availability=availability
    await pg.save()
    res.status(StatusCodes.OK).json({pg})
}

const deletePG=async(req,res)=>{
    const {id:pgId}=req.params
    //one owner can not not delete other owner's pg
    const user=await User.findOne({_id:req.user.userId})
    if (user.role !== "owner" || !user.listings.some(pgItem => pgItem.toString() === pgId)) {
        throw new CustomError.UnautorizedError("You are not authorized to update this PG");
    }
    const pg=await PG.findOne({_id:pgId})
    // const pg=await PG.findOneAndDelete({_id:pgId})
    //Also delete the pgId from the listings of owner
    user.listings = user.listings.filter(pgItem => pgItem.toString() !== pgId);

     // Find all tenants who have booked this PG
    const tenants = await User.find({ "bookedPGs.pgId": pgId });

    // Iterate over each tenant and remove the PG from their bookedPGs
    for (let tenant of tenants) {
        tenant.bookedPGs = tenant.bookedPGs.filter(booking => booking.pgId.toString() !== pgId);
        await tenant.save();
    }
    // when the pg is deleted all the reviews corresponding to that pg are also deleted
    await pg.remove();
    await user.save();
    res.status(StatusCodes.OK).json({msg:'PG deleted successfully',pg})
}


module.exports={
    getAllPGs,
    getSinglePG,
    createPG,
    updatePG,
    deletePG,
}
