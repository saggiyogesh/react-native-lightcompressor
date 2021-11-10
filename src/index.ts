import { NativeEventEmitter, NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-lightcompressor' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const LightCompressor = NativeModules.LightCompressor
  ? NativeModules.LightCompressor
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

const LightCompressorEventEmitter = new NativeEventEmitter(LightCompressor);

export enum VideoQuality {
  VERY_HIGH = 'VERY_HIGH',
  HIGH = 'HIGH',
  MEDIUM = 'MEDIUM',
  LOW = 'LOW',
  VERY_LOW = 'VERY_LOW',
}

type progressEvt = { percent: number };

export interface ICompress {
  uri: string;
  quality?: VideoQuality;
  onProgress?: (progress: number) => void;
  onStart?: () => void;
}

export function compress(options: ICompress): Promise<string> {
  const { uri, quality = VideoQuality.MEDIUM, onProgress, onStart } = options;

  if (onProgress) {
    LightCompressorEventEmitter.addListener(
      'progress',
      (event: progressEvt) => {
        onProgress(event.percent);
      }
    );
  }

  onStart &&
    LightCompressorEventEmitter.addListener('start', () => {
      onStart();
    });

  return LightCompressor.compress(uri, quality);
}

export function cancel(): Promise<string> {
  return LightCompressor.cancel();
}
