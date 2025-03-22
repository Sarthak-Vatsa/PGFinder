const express=require('express')
const router=express.Router()

const {
    getAllPGs,
    getSinglePG,
    createPG,
    updatePG,
    deletePG,
}=require('../controllers/pgController')

const {authenticateUser,authorizePermissions}=require('../middlewares/authentication')

router.route('/').get(authenticateUser,getAllPGs).post(authenticateUser,authorizePermissions('owner'),createPG)
router.route('/:id').get(authenticateUser,getSinglePG).patch(authenticateUser,authorizePermissions('owner'),updatePG).delete(authenticateUser,authorizePermissions('owner'),deletePG)

module.exports=router