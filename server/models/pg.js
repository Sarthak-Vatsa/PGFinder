const mongoose=require('mongoose')
const Reviews = require('./Reviews')

const pgSchema=new mongoose.Schema({
    name:{
        type:String,
        required:[true,'Please provide the name of the PG']
    },
    address:{
        type:String,
        required:[true,'Please provide the address of the PG'],
    },
    city:{
        type:String,
        required:[true,'Please provide the city of the PG']
    },
    price:{
        type:Number,
        required:[true,'Please provide the price of the PG']
    },
    owner:{
        type:mongoose.Types.ObjectId,
        ref:"User" // refer to the owner of the PG
    },
    availability:{
        type:Boolean,
        default:true,
    }
},{timestamps:true})

pgSchema.virtual('reviews',{
    ref:'Reviews',
    localField:'_id',
    foreignField:'pg',
    justOne:false
})

pgSchema.set('toJSON', { virtuals: true });
pgSchema.set('toObject', { virtuals: true });

pgSchema.pre('remove',async function(next){
    await this.model('Reviews').deleteMany({pg:this._id})
})

module.exports=mongoose.model('PG',pgSchema)