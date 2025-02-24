const CustomError=require('../errors')
const User=require('../models/User')
const PG=require('../models/pg')
const Reviews=require('../models/Reviews')
const {StatusCodes}=require('http-status-codes')

const getAllReviews=async(req,res)=>{
    const {pgId}=req.body
    if(!pgId){
        throw new CustomError.BadRequestError('Please provide the pgId')
    }
    const pg=await PG.findOne({_id:pgId})
    if(!pg){
        throw new CustomError.BadRequestError(`The pg with the given id:${pgId} does not exist`)
    }
    const reviews=await Reviews.find({pg:pgId})
    res.status(StatusCodes.OK).json({reviews})
}

const getSingleReview=async(req,res)=>{
    const {id:reviewId}=req.params
    const review=await Reviews.findOne({_id:reviewId})
    if(!review){
        throw new CustomError.NotFoundError('This review does not exist with specified id')
    }
    res.status(StatusCodes.OK).json({review})
}

const createReview=async(req,res)=>{
    const {rating,title,comment,pg}=req.body
    // one user can have only one review for a particular pg
    const alreadyReviewed=await Reviews.findOne({
        user:req.user.userId,
        pg
    })

    if(alreadyReviewed){
        throw new CustomError.BadRequestError('You have already submitted a review corresponding to this PG')
    }

    const review=await Reviews.create({
        rating,
        title,
        comment,
        user:req.user.userId,
        pg
    })
    res.status(StatusCodes.CREATED).json({review})
}

const updateReview=async(req,res)=>{
    const {id:ReviewId}=req.params
    const {rating,title,comment}=req.body
    if(!ReviewId){
        throw new CustomError.BadRequestError('Please provide the reviewId')
    }
    const review=await Reviews.findOne({_id:ReviewId})
    if(!review){
        throw new CustomError.NotFoundError(`The review with the given id:${ReviewId} does not exist`)
    }

    //check permissions a user can not update other user's review
    if(req.user.userId.toString()!==review.user.toString()){
        throw new CustomError.UnautorizedError('You are not allowed to delete other users review')
    }
 

    review.rating=rating
    review.title=title
    review.comment=comment

    await review.save()
    res.status(StatusCodes.OK).json({review})

}

const deleteReview=async(req,res)=>{
    const {id:reviewId}=req.params
    if(!reviewId){
        throw new CustomError.BadRequestError('Please provide the id of the review to be deleted')
    }
    const review=await Reviews.findOne({_id:reviewId})
    if(!review){
        throw new CustomError.NotFoundError(`The review with id:${reviewId} is not found`)
    }
    // a user can not delete other user's review
    if(req.user.userId.toString()!==review.user.toString()){
        throw new CustomError.UnautorizedError('You are not allowed to delete other users review')
    }
    await Reviews.findOneAndDelete({_id:reviewId})
    res.status(StatusCodes.OK).json({msg:'Review deleted successfully',review})
}

module.exports={
    getAllReviews,
    getSingleReview,
    createReview,
    updateReview,
    deleteReview,
}