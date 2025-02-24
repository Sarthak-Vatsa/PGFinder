const express=require('express')
const router=express.Router()

const {
    getAllReviews,
    getSingleReview,
    createReview,
    updateReview,
    deleteReview,
}=require('../controllers/reviewController')

const {authenticateUser}=require('../middlewares/authentication')

router.route('/').get(getAllReviews).post(authenticateUser,createReview)
router.route('/:id').get(getSingleReview).patch(authenticateUser,updateReview).delete(authenticateUser,deleteReview)

module.exports=router





