# @react-native-hero/qiniu

## Getting started

Install the library using either Yarn:

```
yarn add @react-native-hero/qiniu
```

or npm:

```
npm install --save @react-native-hero/qiniu
```

## Link

- React Native v0.60+

For iOS, use `cocoapods` to link the package.

run the following command:

```
$ cd ios && pod install
```

For android, the package will be linked automatically on build.

- React Native <= 0.59

run the following command to link the package:

```
$ react-native link @react-native-hero/qiniu
```

## Example

```js
import {
  CODE,
  ZONE,
  upload,
} from '@react-native-hero/qiniu'

upload(
  {
    // 上传到云端的文件名
    key: 'key',
    // 上传文件的本地路径
    path: '/xx/xx/1.png',
    // 上传文件的 mime type
    mimeType: 'image/png',
    // 上传凭证，通常由服务器生成传给客户端
    token: '',
    // 机房，传入 ZONE 的枚举值
    zone: ZONE.HUABEI,
  },
  // 如果需要获取上传进度
  // 传入第二个参数，progress 取值范围为 0-1
  // 如果不需要获取上传进度，最好不传此参数，避免 js/native 频繁通信
  function (progress) {
    // [0, 1]
  }
)
.then(data => {
  // 上传成功，data 是七牛服务器返回的数据
})
.catch(err => {
  if (err.code === CODE.UPLOAD_FAILURE) {
    console.log('upload error')
  }
})
```
