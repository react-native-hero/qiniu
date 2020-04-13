
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
  HUADONG: RNTQiniu.ZONE_HUADONG,
  HUABEI: RNTQiniu.ZONE_HUABEI,
  HUANAN: RNTQiniu.ZONE_HUANAN,
  BEIMEI: RNTQiniu.ZONE_BEIMEI,
}

export const CODE = {
  UPLOAD_FAILURE: RNTQiniu.ERROR_CODE_UPLOAD_FAILURE,
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
