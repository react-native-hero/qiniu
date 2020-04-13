
import { NativeModules, NativeEventEmitter } from 'react-native'

const { RNTQiniu } = NativeModules

const eventEmitter = new NativeEventEmitter(RNTQiniu)

const index2callback = {}

let count = 0

eventEmitter.addListener('progress', function (data) {
  let onProgress = index2callback[data.index]
  if (onProgress) {
    onProgress(data.progress)
  }
})

export const ZONE = {
  HUADONG: RNTQiniu.QINIU_ZONE_HUADONG,
  HUABEI: RNTQiniu.QINIU_ZONE_HUABEI,
  HUANAN: RNTQiniu.QINIU_ZONE_HUANAN,
  BEIMEI: RNTQiniu.QINIU_ZONE_BEIMEI,
}

export const CODE = {
  UPLOAD_FAILURE: RNTQiniu.QINIU_ERROR_CODE_UPLOAD_FAILURE,
}

export function upload(options, onProgress) {
  if (onProgress) {
    options.index = ++count
    index2callback[options.index] = onProgress
  }
  return RNTQiniu.upload(options)
    .finally(() => {
      if (onProgress) {
        delete index2callback[options.index]
      }
    })
}
