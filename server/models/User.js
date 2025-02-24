const mongoose=require('mongoose')
const bcrypt=require('bcryptjs')
const validator=require('validator')
const Reviews=require('./Reviews')
const { required } = require('joi')

const UserSchema=new mongoose.Schema({
    name:{
        type:String,
        required:[true,'Please provide your name']
    },
    email:{
        type:String,
        required:[true,'Please provide the emai'],
        validate:{
            validator:validator.isEmail,
            message:'Please provide valid email',
        },
        unique:true,
    },
    password:{
        type:String,
        required:[true,'Please provide the password'],
        minlength:6,
    },
    role:{
        type:String,
        enum:["tenant","owner","admin"],
        required:[true,'Please provide the role'],
    },
    listings: [
        {
            type: mongoose.Types.ObjectId,
            ref: "PG" // PGs listed by owners
        }
    ],
    bookedPGs: [
        {
            pgId: { type: mongoose.Types.ObjectId, ref: "PG" },
            status: {
                type: String,
                enum: ["pending", "approved", "rejected"],
                default: "pending"
            }
        }
    ],
},{ timestamps: true })

UserSchema.pre('remove',async function(next){
    await this.model('Reviews').deleteMany({user:this._id})
})

module.exports=mongoose.model('User',UserSchema)
