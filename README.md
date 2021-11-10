# react-native-lightcompressor

video compressor

## Installation
```sh
yarn add react-native-lightcompressor
```


```sh
npm install react-native-lightcompressor
```

## Usage

```js
import { compress, VideoQuality, cancel } from 'react-native-lightcompressor';

// ...

const r = await compress({
  uri: 'file://path_of_file/video.mp4',
  quality: VideoQuality.MEDIUM,
  onProgress: (p: number) => console.log('progress--> ', p),
  onStart: () => console.log('start'),
});
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
