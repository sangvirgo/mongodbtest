const { Timestamp } = require('mongodb')
console.log(new Timestamp({ t: 1, i: 0 }))
var currentTimestamp = new Timestamp(
  1,
  1
);

while (1) {
    console.log('currentTimestamp :>> ', currentTimestamp);
    
    sleep(1000);
}