#import "RNTQiniu.h"
#import <React/RCTConvert.h>
#import <QiniuSDK.h>

NSString *ZONE_HUADONG = @"huadong";
NSString *ZONE_HUABEI = @"huabei";
NSString *ZONE_HUANAN = @"huanan";
NSString *ZONE_BEIMEI = @"beimei";

NSString *ERROR_CODE_UPLOAD_FAILURE = @"1";

@implementation RNTQiniu

RCT_EXPORT_MODULE(RNTQiniu);

- (NSArray<NSString *> *)supportedEvents {
    return @[
        @"progress",
    ];
}

- (NSDictionary *)constantsToExport {
    return @{
        @"QINIU_ZONE_HUADONG": ZONE_HUADONG,
        @"QINIU_ZONE_HUABEI": ZONE_HUABEI,
        @"QINIU_ZONE_HUANAN": ZONE_HUANAN,
        @"QINIU_ZONE_BEIMEI": ZONE_BEIMEI,
        @"QINIU_ERROR_CODE_UPLOAD_FAILURE": ERROR_CODE_UPLOAD_FAILURE,
    };
}

RCT_EXPORT_METHOD(upload:(NSDictionary*)options
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject) {

    int index = [RCTConvert int:options[@"index"]];

    NSString *path = [RCTConvert NSString:options[@"path"]];
    NSString *key = [RCTConvert NSString:options[@"key"]];
    NSString *zone = [RCTConvert NSString:options[@"zone"]];
    NSString *token = [RCTConvert NSString:options[@"token"]];
    NSString *mimeType = [RCTConvert NSString:options[@"mimeType"]];

    QNConfiguration *config = [QNConfiguration
            build:^(QNConfigurationBuilder *builder) {

                builder.useHttps = YES;

                if ([zone isEqual: ZONE_HUADONG]) {
                    builder.zone = [QNFixedZone zone0];
                }
                else if ([zone isEqual: ZONE_HUABEI]) {
                    builder.zone = [QNFixedZone zone1];
                }
                else if ([zone isEqual: ZONE_HUANAN]) {
                    builder.zone = [QNFixedZone zone2];
                }
                else if ([zone isEqual: ZONE_BEIMEI]) {
                    builder.zone = [QNFixedZone zoneNa0];
                }

            }
    ];

    QNUploadManager *uploadManager = [[QNUploadManager alloc] initWithConfiguration:config];

    QNUploadOption *uploadOption = [[QNUploadOption alloc]
            initWithMime:mimeType
            progressHandler:^(NSString *key, float percent) {
                if (index > 0) {
                    [self sendEventWithName:@"progress" body:@{
                        @"index": @(index),
                        @"progress": @(percent),
                    }];
                }
            }
            params:nil
            checkCrc:NO
            cancellationSignal:nil
    ];

    [uploadManager
            putFile:path
            key:key
            token:token
            complete:^(QNResponseInfo *info, NSString *key, NSDictionary *resp) {
                if (info.ok) {
                    resolve(resp);
                }
                else {
                    reject(ERROR_CODE_UPLOAD_FAILURE, info.error.localizedDescription, info.error);
                }
            }
            option:uploadOption
     ];

}

@end
