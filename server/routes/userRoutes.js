const express=require('express')
const router=express.Router()

const {getAllUsers,getSingleUser,showCurrentUser,updatePassword,updateUser,bookPG,deleteUser}=require('../controllers/userContoller')

const {authenticateUser,authorizePermissions}=require('../middlewares/authentication')

router.get('/',authenticateUser,authorizePermissions('admin','owner'),getAllUsers)
router.route('/showMe').get(authenticateUser,showCurrentUser)
router.route('/updateUser').patch(authenticateUser,updateUser)
router.route('/deleteUser').delete(authenticateUser,deleteUser)
router.route('/bookPG').post(authenticateUser,bookPG)
// router.route('/handleBookingRequest').post(authenticateUser,authorizePermissions('owner'),handleBookingRequest)
router.route('/updatePassword').patch(authenticateUser,updatePassword)

router.route('/:id').get(authenticateUser,getSingleUser)

module.exports=router