
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat.Encoding;

public class StereoInputStream extends AudioInputStream{
		protected int inputChannels;
		protected AudioFormat newFormat;
		
		public StereoInputStream(AudioInputStream input) {
			super(input, input.getFormat(), input.getFrameLength());
			this.newFormat = new AudioFormat(input.getFormat().getEncoding(), input.getFormat().getSampleRate(), input.getFormat()
					.getSampleSizeInBits(), 2, 2 * input.getFormat().getFrameSize() / input.getFormat().getChannels(), input
					.getFormat().getFrameRate(), input.getFormat().isBigEndian());
			this.inputChannels = input.getFormat().getChannels();
		}
		
		public int readConvertLeftAudio(byte[] b,int off, int len)throws Exception{
			int sampleSizeInBytes = newFormat.getFrameSize() / newFormat.getChannels();
			int outputFrameSize = sampleSizeInBytes * 2;
			int nFrames = len / outputFrameSize;
			byte[] inputBytes = new byte[nFrames * newFormat.getFrameSize()];
			int nInputBytes = super.read(inputBytes, 0, inputBytes.length);
			
			if (nInputBytes <= 0)
				return nInputBytes;
	    	if (!newFormat.getEncoding().equals(Encoding.PCM_SIGNED)) {
				throw new IllegalArgumentException("Channel muting supported only for PCM_SIGNED encoding, got "
						+ newFormat.getEncoding());
			}
			for (int i = 0, j = off; i < nInputBytes; i += frameSize, j += outputFrameSize) {
				for (int k = 0; k < sampleSizeInBytes; k++) {
					b[j + k] = inputBytes[i + k];
					b[j + sampleSizeInBytes + k] = 0;
				}
			}
			return 2 * nInputBytes / inputChannels;
	    }
		
		public int readConvertRightAudio(byte[] b,int off, int len)throws Exception{
			int sampleSizeInBytes = newFormat.getFrameSize() / newFormat.getChannels();
			int outputFrameSize = sampleSizeInBytes * 2;
			int nFrames = len / outputFrameSize;
			byte[] inputBytes = new byte[nFrames * newFormat.getFrameSize()];
			int nInputBytes = super.read(inputBytes, 0, inputBytes.length);
			
			if (nInputBytes <= 0)
				return nInputBytes;
			if (!getFormat().getEncoding().equals(Encoding.PCM_SIGNED)) {
				throw new IllegalArgumentException("Channel muting supported only for PCM_SIGNED encoding, got "
						+ getFormat().getEncoding());
			}
			for (int i = 0, j = off; i < nInputBytes; i += frameSize, j += outputFrameSize) {
				for (int k = 0; k < sampleSizeInBytes; k++) {
					b[j + k] = 0;
					b[j + sampleSizeInBytes + k] = inputBytes[i + k];
				}
			}
			return 2 * nInputBytes / inputChannels;
	    }
	}
