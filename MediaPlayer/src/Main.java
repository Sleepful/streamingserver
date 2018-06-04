import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
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
	
	static int screenWidth;
	static int screenHeight;
	
	static JFrame video_frame;
	static JFrame songs_frame;
	static JFrame controls_frame;
	static JFrame playlists_frame;
	
	public static void main(String[] args){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenWidth = screenSize.width;
		screenHeight = screenSize.height;
		
		create_user();
		create_video_frame();
		create_controls_frame();
		create_songs_frame();
		create_playlists_frame();
	}
	
	private static void create_user() {
		user = new User();
		
		user.add_playlist("Rock");
		user.add_playlist("Pop");
		user.add_playlist("Jazz");
		user.add_playlist("Disco");
		user.add_playlist("Rap");
		user.add_playlist("VGM");
		
		//ESTOS LOS TIENE QUE PEDIR AL SERVIDOR DEPENDIENDO DEL CURRENT_PLAYLIST
		user.add_rtsp("rtsp://184.72.239.149/vod/mp4:BigBuckBunny_175k.mov");
		user.add_rtsp("rtsp://184.72.239.149/vod/mp4:BigBuckBunny_175k.mov");
		user.add_rtsp("rtsp://184.72.239.149/vod/mp4:BigBuckBunny_175k.mov");
		user.add_rtsp("rtsp://184.72.239.149/vod/mp4:BigBuckBunny_175k.mov");
		user.add_rtsp("rtsp://184.72.239.149/vod/mp4:BigBuckBunny_175k.mov");
		user.add_rtsp("rtsp://184.72.239.149/vod/mp4:BigBuckBunny_175k.mov");
		user.add_rtsp("rtsp://184.72.239.149/vod/mp4:BigBuckBunny_175k.mov");
		user.add_rtsp("rtsp://184.72.239.149/vod/mp4:BigBuckBunny_175k.mov");
		user.add_rtsp("rtsp://184.72.239.149/vod/mp4:BigBuckBunny_175k.mov");
		user.add_rtsp("rtsp://184.72.239.149/vod/mp4:BigBuckBunny_175k.mov");
	}
	
	private static void create_playlists_frame() {
		playlists_frame = new JFrame();
		playlists_frame.setLocation((screenWidth/2)-(screenWidth/4),(screenHeight/2)-(screenHeight/4)-(screenHeight/8));
		playlists_frame.setSize((screenWidth/2),(screenHeight/8));
		playlists_frame.setVisible(true);
		
		JPanel playlists_panel = new JPanel(new GridLayout(1,1+(user.playlists_list.size())));
		playlists_panel.setLocation(playlists_frame.getX(),playlists_frame.getY());
		
		JLabel tab_label = new JLabel("Tab:",JLabel.CENTER);
		playlists_panel.add(tab_label);
		
		for(int i=0;i<user.playlists_list.size();i++) {
			String temp_playlist = user.playlists_list.get(i);
			JLabel new_label = new JLabel(temp_playlist,JLabel.CENTER);
			playlists_panel.add(new_label);
		}
		
		playlists_frame.add(playlists_panel);
	}
	
	private static void create_controls_frame() {
		controls_frame = new JFrame();
		controls_frame.setLocation((screenWidth/2)-(screenWidth/4)-(screenWidth/8),(screenHeight/2)-(screenHeight/4));
		controls_frame.setSize((screenWidth/8),(screenHeight/2));
		controls_frame.setVisible(true);
		
		JPanel controls_panel = new JPanel(new GridLayout(4,1));
		controls_panel.setLocation(controls_frame.getX(),controls_frame.getY());
		
		JLabel pause_label = new JLabel("P: Pause",JLabel.CENTER);
		controls_panel.add(pause_label);
		JLabel shuffle_label = new JLabel("S: Shuffle",JLabel.CENTER);
		controls_panel.add(shuffle_label);
		JLabel next_label = new JLabel("N: Next",JLabel.CENTER);
		controls_panel.add(next_label);
		JLabel restart_label = new JLabel("R: Restart",JLabel.CENTER);
		controls_panel.add(restart_label);
		
		controls_frame.add(controls_panel);
	}
	
	private static void create_songs_frame() {
		songs_frame = new JFrame();
		songs_frame.setLocation((screenWidth/2)+(screenWidth/4),(screenHeight/2)-(screenHeight/4));
		songs_frame.setSize((screenWidth/8),(screenHeight/2));
		songs_frame.setVisible(true);
		
		JPanel songs_panel = new JPanel(new GridLayout(user.rtsp_list.size(),1));
		songs_panel.setLocation(songs_frame.getX(),songs_frame.getY());
		
		for(int i=0;i<user.rtsp_list.size();i++) {
			//String temp_rtsp = user.rtsp_list.get(i);
			JLabel new_label = new JLabel(i+": Song name",JLabel.CENTER);
			songs_panel.add(new_label);
		}
		
		songs_frame.add(songs_panel);
	}
	
	private static void create_video_frame(){
		///GUI
		//Create Window
		video_frame = new JFrame();
		video_frame.setLocation((screenWidth/4),(screenHeight/4));
		video_frame.setSize(screenWidth/2,screenHeight/2);
		video_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		video_frame.setVisible(true);
		
		//Create Canvas
		Canvas c = new Canvas();
		c.setBackground(Color.black);
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(c);
		video_frame.add(p);
		
		///VLCJ
		//Load Library
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(),"lib");
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(),LibVlc.class);
		//Initialize Media Player
		MediaPlayerFactory mpf = new MediaPlayerFactory();
		//Control User Interactions
		emp = mpf.newEmbeddedMediaPlayer(new Win32FullScreenStrategy(video_frame));
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
		video_frame.addKeyListener(key_listener);
		video_frame.setFocusTraversalKeysEnabled(false);
		c.addKeyListener(key_listener);
		c.setFocusTraversalKeysEnabled(false);
		p.addKeyListener(key_listener);
		p.setFocusTraversalKeysEnabled(false);
		
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
		if (evt.getKeyChar() == 'p') {
			if(emp.isPlaying()) {
				emp.pause();
			}else {
				emp.play();
			}
		}
		
		if (evt.getKeyChar() == '0'){
			user.go_to_index_rtsp(emp, 0);
		}
		if (evt.getKeyChar() == '1'){
			user.go_to_index_rtsp(emp, 1);
		}
		if (evt.getKeyChar() == '2'){
			user.go_to_index_rtsp(emp, 2);
		}
		if (evt.getKeyChar() == '3'){
			user.go_to_index_rtsp(emp, 3);
		}
		if (evt.getKeyChar() == '4'){
			user.go_to_index_rtsp(emp, 4);
		}
		if (evt.getKeyChar() == '5'){
			user.go_to_index_rtsp(emp, 5);
		}
		if (evt.getKeyChar() == '6'){
			user.go_to_index_rtsp(emp, 6);
		}
		if (evt.getKeyChar() == '7'){
			user.go_to_index_rtsp(emp, 7);
		}
		if (evt.getKeyChar() == '8'){
			user.go_to_index_rtsp(emp, 8);
		}
		if (evt.getKeyChar() == '9'){
			user.go_to_index_rtsp(emp, 9);
		}
		
	    if (evt.getKeyCode() == KeyEvent.VK_TAB) {
	        user.go_to_next_playlist();
	    }
	}
}