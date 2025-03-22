const User=require('../models/User')
const {StatusCodes}=require('http-status-codes')
const CustomError=require('../errors')
const { attachCookieToResponse } = require('../utils/jwt')

const register=async(req,res)=>{
    const {name,email,password,role}=req.body
    let existingUser=await User.findOne({email})
    if(existingUser){
        throw new CustomError.BadRequestError('Email already exists')
    }
    const user=await User.create({name,email,password,role})
    const tokenUser={name:user.name,userId:user._id,role:user.role}
    attachCookieToResponse({res,user:tokenUser})
}

const login=async(req,res)=>{
    const {email,password}=req.body
    if(!email || !password){
        throw new CustomError.BadRequestError('Please provide your email and password')
    }
    const user=await User.findOne({email})
    if(!user){
        throw new CustomError.UnauthenticatedError('Invalid credentials')
    }
    if(user.password!==password){
        throw new CustomError.UnauthenticatedError('Invalid Credentials')
    }

    const tokenUser={name:user.name,userId:user._id,role:user.role}
    attachCookieToResponse({res,user:tokenUser})
    res.status(StatusCodes.OK).json({user:tokenUser})
}

const logout=async(req,res)=>{
    console.log("Logout Reached")
    // expire the cookie
    res.cookie('token','logout',{
        httpOnly:true,
        expires:new Date(Date.now())
    })
    res.status(StatusCodes.OK).json({msg:'user logged out'})
}

module.exports={
    register,
    login,
    logout,
}
