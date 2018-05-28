import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.windows.Win32FullScreenStrategy;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;


public class Main {
	
	static EmbeddedMediaPlayer emp;
	static User user;
	
	public static void main(String[] args){
		create_user();
		create_video();
	}
	
	private static void create_user() {
		user = new User();
		user.add_rtsp("rtsp://184.72.239.149/vod/mp4:BigBuckBunny_175k.mov");
		user.add_rtsp("rtsp://184.72.239.149/vod/mp4:BigBuckBunny_175k.mov");
		user.add_rtsp("rtsp://184.72.239.149/vod/mp4:BigBuckBunny_175k.mov");
	}
	
	private static void create_video(){
		///GUI
		//Create Window
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = screenSize.width;
		int screenHeight = screenSize.height;
		JFrame f = new JFrame();
		f.setLocation((screenWidth/4),(screenHeight/4));
		f.setSize(screenWidth/2,screenHeight/2);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		
		//Create Canvas
		Canvas c = new Canvas();
		c.setBackground(Color.black);
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(c);
		f.add(p);
		
		///VLCJ
		//Load Library
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(),"lib");
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(),LibVlc.class);
		//Initialize Media Player
		MediaPlayerFactory mpf = new MediaPlayerFactory();
		//Control User Interactions
		emp = mpf.newEmbeddedMediaPlayer(new Win32FullScreenStrategy(f));
		emp.setVideoSurface(mpf.newVideoSurface(c));
		//Full Screen
		//emp.toggleFullScreen();
		//Hide Cursor
		emp.setEnableMouseInputHandling(false);
		//Disable Keyboard
		emp.setEnableKeyInputHandling(false);
		
		String file = user.current_rtsp;
		//Prepare File
		emp.prepareMedia(file);
		//Read the File
		emp.play();
		
		//Add Key Listener
		KeyListener key_listener = new KeyListener(emp,user);
		f.addKeyListener(key_listener);
		c.addKeyListener(key_listener);
		p.addKeyListener(key_listener);
		
		emp.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
		    @Override
		    public void finished(MediaPlayer mediaPlayer) {
		        user.go_to_next_rtsp(emp);
		    }
		});
	}
}

class KeyListener extends KeyAdapter {
	
	EmbeddedMediaPlayer emp;
	User user;
	
	public KeyListener(EmbeddedMediaPlayer emp, User user) {
		this.emp = emp;
		this.user = user;
	}
	
	public void keyPressed(KeyEvent evt) {
		if (evt.getKeyChar() == 'r'){
			emp.stop();
			emp.play();
		}
		if (evt.getKeyChar() == 's'){
			user.shuffle = !user.shuffle;
		}
		if (evt.getKeyChar() == 'n') {
			user.go_to_next_rtsp(emp);
		}
		if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
			if(emp.isPlaying()) {
				emp.pause();
			}else {
				emp.play();
			}
		}
	}
}