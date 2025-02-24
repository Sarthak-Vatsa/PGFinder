const CustomError=require('../errors')

const checkPermissions=(reqUser,resorceId)=>{
    // will refine in the way that an owner can only see the profiles of his/her tenants and not every tenant
    if(reqUser.role==='admin') return 
    if(reqUser.userId===resorceId.toString()) return
    if(reqUser.role==='owner') return
    throw new CustomError.UnautorizedError('not authorized to access this route')
}

module.exports=checkPermissions