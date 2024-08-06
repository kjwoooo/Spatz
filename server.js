const express = require('express');
const path = require('path');
const cors = require('cors');

const app = express();
const port = 3000;

app.use(cors({
    origin:  ['http://localhost:3000', 'http://localhost:8080'],
    credentials: true
}));


// node_modules 폴더를 정적 파일로 제공
app.use('/node_modules', express.static(path.join(__dirname, 'node_modules')));

// static 폴더를 정적 파일로 제공
app.use(express.static(path.join(__dirname, 'src/main/resources/static')));


app.listen(port, () => {
    console.log(`Server is running at http://localhost:${port}`);
});
