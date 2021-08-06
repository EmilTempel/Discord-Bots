package discord;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;

import net.dv8tion.jda.api.audio.AudioSendHandler;

public class AudioHandler implements AudioSendHandler {

	static AudioFormat Format = new AudioFormat(1000f, 16, 2, true, true);

	
	
	ArrayList<ByteBuffer> b;

	public AudioHandler() {
		b = new ArrayList<ByteBuffer>();
	}

	public void play(String path) {
		try {
			File f = new File(path);
			InputStream ais = new FileInputStream(f);
			
			int length = (int) f.length();
			
			float sample_len = 1000f / Format.getFrameRate();
			int sample_size = Format.getFrameSize();

			int delta = Math.round(sample_size * (20f / sample_len));

			for (int i = 0; i < length; i += delta) {
				if (i + delta < length) {
					byte[] temp = new byte[delta];
					
					ais.read(temp, 0, delta);
					
					b.add((ByteBuffer) ByteBuffer.wrap(temp).flip());
				}
			}
			
			ais.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean canProvide() {
		return b.size() != 0;
	}

	public ByteBuffer provide20MsAudio() {
		ByteBuffer snippet = b.get(b.size()-1);
		b.remove(b.size()-1);
		System.out.println(b.size());
		return snippet;
	}
	
	public boolean isOpus() {
		return false;
		
	}
}
