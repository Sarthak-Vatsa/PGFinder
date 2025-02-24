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

router.route('/').get(getAllPGs).post(authenticateUser,authorizePermissions('owner'),createPG)
router.route('/:id').get(getSinglePG).patch(authenticateUser,authorizePermissions('owner'),updatePG).delete(authenticateUser,authorizePermissions('owner'),deletePG)

module.exports=router