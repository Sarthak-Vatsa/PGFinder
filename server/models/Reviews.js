const mongoose=require('mongoose')

const ReviewSchema=new mongoose.Schema({
    rating:{
        type:Number,
        min:1,
        max:5,
        required:[true,'Please provide the rating']
    },
    title:{
        type:String,
        trim:true,
        maxlength:100,
        required:[true,'Please provide the title']
    },
    comment:{
        type:String,
        required:[true,'Please provide the comment text'],
    },
    user:{
        type:mongoose.Types.ObjectId,
        ref:"User",
        required:true
    },
    pg:{
        type:mongoose.Types.ObjectId,
        ref:"PG",
        required:true,
    }
})

module.exports=mongoose.model('Reviews',ReviewSchema)