const {createJWT,isTokenValid,attachCookieToResponse}=require('./jwt')
const checkPermissions=require('./checkPermissions')

module.exports={
    createJWT,
    isTokenValid,
    attachCookieToResponse,
    checkPermissions,
}