require('dotenv').config()
require('express-async-errors') // we do not have to write the try and catch block
const express=require('express')
const app=express()
const cookieParser=require('cookie-parser')
const authRouter=require('./routes/authRoutes')
const userRouter=require('./routes/userRoutes')
const pgRouter=require('./routes/pgRoutes')
const reviewRouter=require('./routes/reviewRoutes')
const cors=require('cors')
const errorHandlerMiddleware=require('./middlewares/error-handler')
const notFoundMiddleware=require('./middlewares/notFound')

//connect database
const connectDB=require('./db/connect')


//middlewares
app.use(express.json())
app.use(cookieParser(process.env.JWT_SECRET)) // signed cookie
app.use(cors())


//routes
app.use('/api/v1/auth',authRouter)
app.use('/api/v1/user',userRouter)
app.use('/api/v1/pg',pgRouter)
app.use('/api/v1/reviews',reviewRouter)


app.use(notFoundMiddleware)
app.use(errorHandlerMiddleware)

const port=process.env.PORT || 3001
const start=async()=>{
    try {
        await connectDB(process.env.MONGO_URI)
        app.listen(port,console.log(`Server is listining at port ${port}...`))
        
    } catch (error) {
        console.log(error);
        
    }
}

start()


