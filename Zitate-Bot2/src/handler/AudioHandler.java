package handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFileFormat.Type;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.AudioPlayer;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;

import discord.UserInformation;
import discord.Zitat;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class AudioHandler extends Handler implements AudioSendHandler {

	static AudioFormat Format = new AudioFormat(1000f, 16, 2, true, true);

	ArrayList<ByteBuffer> b;

	boolean randomZitatAudio = false;
	Timer t;
	final int periodMillis = 500000;
	final double probability = 1;

	AudioHandler audio;
	AudioManager manager;

	ZitatHandler zitathandler;

	public AudioHandler(ZitatHandler zitathandler, UserInformation userinfo) {
		super(zitathandler.g, userinfo);
		b = new ArrayList<ByteBuffer>();
		this.zitathandler = zitathandler;
	}

	public void saveStringAsWav(String s, String filename) {
		Voice voice = VoiceManager.getInstance().getVoice("kevin16");
		if (voice != null) {
			voice.allocate();

			AudioPlayer audioplayer = new SingleFileAudioPlayer(filename, Type.AU);
			audioplayer.setAudioFormat(AudioHandler.Format);
			voice.setAudioPlayer(audioplayer);
			try {

				voice.setRate(150);
				voice.setPitch(120);
				voice.setVolume(3);
				voice.speak(s);

			} catch (Exception e) {
				e.printStackTrace();
			}

			audioplayer.close();
		}
	}

	public void saveZitatAsWav(Zitat z, String filename) {
		saveStringAsWav(z.getAll(), filename);
	}

	public void saveRandomZitatAsTTSOutputWav() {
		saveZitatAsWav(zitathandler.randomZitat(), "TTSOutput");
	}

	public void cmdToggleRandomZitatAudio(GuildMessageReceivedEvent e, String[] cmd_body) {
		e.getChannel().sendMessage("gibts noch ned, Vollpfosten!");

		randomZitatAudio = (randomZitatAudio) ? false : true;

		if (audio == null || manager == null) {
			manager = e.getGuild().getAudioManager();
			audio = this;

			manager.setSendingHandler(audio);
		}

		if (randomZitatAudio) {
			randomlyJoinRandomOccupiedChannel(e);
		} else {
			t.cancel();
		}
	}

	public VoiceChannel randomOccupiedVoiceChannel(GuildMessageReceivedEvent e) {
		List<VoiceChannel> vcs = e.getGuild().getVoiceChannels();
		ArrayList<VoiceChannel> ocVcs = new ArrayList<VoiceChannel>();

		for (int i = 0; i < vcs.size(); i++) {
			if (vcs.get(i).getMembers().size() != 0) {
				ocVcs.add(vcs.get(i));
			}
		}

		VoiceChannel erg = ocVcs.get((int) (Math.random() * ocVcs.size()));

		return erg;
	}

	public void joinChannel(GuildMessageReceivedEvent e, VoiceChannel vc) {
		TextChannel tc = e.getChannel();

		if (!e.getGuild().getSelfMember().hasPermission(tc, Permission.VOICE_CONNECT)) {
			tc.sendMessage("Darf nicht connecten, Depp!").queue();
			return;
		}

		if (vc == null) {
			tc.sendMessage("gibt keinen VoiceChannel, Doofbeutel!").queue();
		}

		e.getGuild().getAudioManager().openAudioConnection(vc);
	}

	public void joinRandomOccupiedChannel(GuildMessageReceivedEvent e) {
		joinChannel(e, randomOccupiedVoiceChannel(e));
	}

	public void randomlyJoinRandomOccupiedChannel(GuildMessageReceivedEvent e) {
		t = new Timer();
		t.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				if (Math.random() < probability) {
					VoiceChannel vc = randomOccupiedVoiceChannel(e);

					saveRandomZitatAsTTSOutputWav();
					manager.openAudioConnection(vc);
					audio.play("TTSOutput.opus");
				}
			}

		}, 0, periodMillis);
	}

	public void leaveChannel(GuildMessageReceivedEvent e) {
		e.getGuild().getAudioManager().closeAudioConnection();
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

					b.add(ByteBuffer.wrap(temp));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean canProvide() {
		return b.size() != 0;
	}

	public ByteBuffer provide20MsAudio() {
		ByteBuffer snippet = b.get(b.size() - 1);
		b.remove(b.size() - 1);
		System.out.println(b.size());
		return snippet;
	}

	public boolean isOpus() {
		return true;

	}
}
