import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.print.attribute.standard.MediaSize.Other;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.windows.Win32FullScreenStrategy;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import java.util.ArrayList;
import java.util.Objects;

import org.zeromq.ZMQ;

import java.util.Scanner;


public class Main {
	
	static EmbeddedMediaPlayer emp;
	static User user;
	
	static int screenWidth;
	static int screenHeight;
	
	static JFrame video_frame;
	static JFrame songs_frame;
	static JFrame controls_frame;
	static JFrame playlists_frame;
	
	static ZMQ.Socket requester;
	
	static String ip;
	static String port;
	static String video_port;
	
	public static void main(String[] args){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenWidth = screenSize.width;
		screenHeight = screenSize.height;
		
		ip = "206.189.228.117";
		port = "5555";
		video_port = "8554";
		
		create_socket();
		create_user();
		create_playlists_frame();
		create_songs_frame();
		create_controls_frame();
		create_video_frame();
	}
	
	public static void create_socket() {
		ZMQ.Context context = ZMQ.context(1);
        requester = context.socket(ZMQ.REQ);
        requester.connect("tcp://"+ip+":"+port);
	}
	
	public static void create_user() {
		Scanner reader = new Scanner(System.in);
		System.out.print("Please enter your username: ");
		String name = reader.nextLine();
		reader.close();
		
		user = new User(name);
		
		byte[] reply = null;
        String request = "user:"+name;
        requester.send(request.getBytes(), 0);
        reply = requester.recv(0);
        System.out.println(new String(reply));
		
        ArrayList<Integer> all_playlist = new ArrayList<Integer>();
        reply = null;
        int count=0;
        while(reply == null || !Objects.equals("end:",new String(reply))) {
	        request = "urls:"+count;
	        requester.send(request.getBytes(), 0);
	        reply = requester.recv(0);
	        if(!Objects.equals("end:",new String(reply))) {
	        	all_playlist.add(count);
	        }
	        count++;
        }
        
        user.add_playlist("All",all_playlist,requester);
        
	}
	
    public static void create_playlists_frame() {
        playlists_frame = new JFrame();
        playlists_frame.setLocation((screenWidth/2)-(screenWidth/4),(screenHeight/2)-(screenHeight/4)-(screenHeight/8));
        playlists_frame.setSize((screenWidth/2),(screenHeight/8));
        playlists_frame.setVisible(true);
        
        JPanel playlists_panel = new JPanel(new GridLayout(1,user.playlist_names_list.size()));
        playlists_panel.setLocation(playlists_frame.getX(),playlists_frame.getY());
        
        for(int i=0;i<user.playlist_names_list.size();i++) {
            String temp_playlist_name = user.playlist_names_list.get(i);
            JLabel new_label = new JLabel(temp_playlist_name,JLabel.CENTER);
            playlists_panel.add(new_label);
        }
        
        playlists_frame.add(playlists_panel);
    }
    
	public static void create_songs_frame() {
		songs_frame = new JFrame();
		songs_frame.setLocation((screenWidth/2)+(screenWidth/4),(screenHeight/2)-(screenHeight/4));
		songs_frame.setSize((screenWidth/8),(screenHeight/2));
		songs_frame.setVisible(true);
		
		JPanel songs_panel = new JPanel(new GridLayout(user.rtsp_list.size(),1));
		songs_panel.setLocation(songs_frame.getX(),songs_frame.getY());
		
        byte[] reply = null;
        for(int i=0;i<user.current_playlist.size();i++){
        	int temp_song_number = user.current_playlist.get(i);
	        String request = "name:"+temp_song_number;
	        requester.send(request.getBytes(), 0);
	        reply = requester.recv(0);
	        if(!Objects.equals("end:",new String(reply))) {
				JLabel new_label = new JLabel(i+"."+new String(reply),JLabel.CENTER);
				songs_panel.add(new_label);
	        }
        }
		
		songs_frame.add(songs_panel);
	}
	
	public static void create_controls_frame() {
		controls_frame = new JFrame();
		controls_frame.setLocation((screenWidth/2)-(screenWidth/4)-(screenWidth/8),(screenHeight/2)-(screenHeight/4));
		controls_frame.setSize((screenWidth/8),(screenHeight/2));
		controls_frame.setVisible(true);
		
		JPanel controls_panel = new JPanel(new GridLayout(6,1));
		controls_panel.setLocation(controls_frame.getX(),controls_frame.getY());
		
		JLabel pause_label = new JLabel("P: Pause",JLabel.CENTER);
		controls_panel.add(pause_label);
		JLabel shuffle_label = new JLabel("S: Shuffle",JLabel.CENTER);
		controls_panel.add(shuffle_label);
		JLabel next_label = new JLabel("N: Next",JLabel.CENTER);
		controls_panel.add(next_label);
		JLabel restart_label = new JLabel("R: Restart",JLabel.CENTER);
		controls_panel.add(restart_label);
		JLabel new_playlist_label = new JLabel("+: New Playlist",JLabel.CENTER);
		controls_panel.add(new_playlist_label);
		JLabel next_playlist_label = new JLabel("Tab: Next Playlist",JLabel.CENTER);
		controls_panel.add(next_playlist_label);
		
		controls_frame.add(controls_panel);
	}
	
	public static void create_video_frame(){
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
	String playlist_name;
	
	public KeyListener(EmbeddedMediaPlayer emp, User user) {
		this.emp = emp;
		this.user = user;
	}
	
	public void keyPressed(KeyEvent evt) {
		if (evt.getKeyChar() == 'r' || evt.getKeyChar() == 'R'){
			emp.stop();
			emp.play();
		}
		if (evt.getKeyChar() == 's' || evt.getKeyChar() == 'S'){
			user.shuffle = !user.shuffle;
		}
		if (evt.getKeyChar() == 'n' || evt.getKeyChar() == 'N') {
			user.go_to_next_rtsp(emp);
		}
		if (evt.getKeyChar() == 'p' || evt.getKeyChar() == 'P') {
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
		
        if (evt.getKeyChar() == '+') {
        	emp.stop();
        	user.change_playlist(0, Main.requester);
            Main.songs_frame.dispose();
            Main.create_songs_frame();
        	create_new_playlist_menu();
        }
		
        if (evt.getKeyCode() == KeyEvent.VK_TAB) {
            user.go_to_next_playlist(Main.requester);
            Main.songs_frame.dispose();
            Main.create_songs_frame();
        }
	}
	
	private void create_new_playlist_menu() {
		ask_for_playlist_name();
	}
	
	public void ask_for_playlist_name() {
        JFrame new_playlist_frame = new JFrame();
        new_playlist_frame.setLocation((Main.screenWidth/2)-((Main.screenWidth/8)),(Main.screenHeight/2)-(Main.screenHeight/8));
        new_playlist_frame.setSize((Main.screenWidth/4),(Main.screenHeight/4));
        new_playlist_frame.setVisible(true);
        
        JPanel new_playlist_panel = new JPanel(new GridLayout(3,1));
        new_playlist_panel.setLocation(new_playlist_panel.getX(),new_playlist_panel.getY());
        
        JLabel name_label = new JLabel("Please enter the name of the new playlist");
        new_playlist_panel.add(name_label);
        
        JTextField name_textfield = new JTextField();
        new_playlist_panel.add(name_textfield);
        
        JButton name_button = new JButton("Ok");
        name_button.addActionListener(new ActionListener() { 
        	  public void actionPerformed(ActionEvent e) {
        		  playlist_name = name_textfield.getText();
        		  ask_for_song_numbers();
        		  new_playlist_frame.dispose();
        	  } 
        	  });
        new_playlist_panel.add(name_button);
        
        new_playlist_frame.add(new_playlist_panel);
	}
	
	public void ask_for_song_numbers() {
		ArrayList<Integer> new_playlist = new ArrayList<Integer>();
		
        JFrame new_playlist_frame = new JFrame();
        new_playlist_frame.setLocation((Main.screenWidth/2)-((Main.screenWidth/8)),(Main.screenHeight/2)-(Main.screenHeight/8));
        new_playlist_frame.setSize((Main.screenWidth/4),(Main.screenHeight/4));
        new_playlist_frame.setVisible(true);
        
        JPanel new_playlist_panel = new JPanel(new GridLayout(5,1));
        new_playlist_panel.setLocation(new_playlist_panel.getX(),new_playlist_panel.getY());
        
        JLabel name_label = new JLabel("Please enter a song number and press add");
        new_playlist_panel.add(name_label);
        
        JTextField name_textfield = new JTextField();
        new_playlist_panel.add(name_textfield);
        
        JButton add_button = new JButton("Add");
        add_button.addActionListener(new ActionListener() { 
        	  public void actionPerformed(ActionEvent e) {
        		  new_playlist.add(Integer.parseInt(name_textfield.getText()));
        		  name_textfield.setText("");
        	  } 
        	  });
        new_playlist_panel.add(add_button);
        
        JButton ok_button = new JButton("Ok");
        ok_button.addActionListener(new ActionListener() { 
        	  public void actionPerformed(ActionEvent e) {
        		  Main.user.add_playlist(playlist_name, new_playlist, Main.requester);
	              Main.playlists_frame.dispose();
	              Main.create_playlists_frame();
        		  new_playlist_frame.dispose();
        	  } 
        	  });
        new_playlist_panel.add(ok_button);
        
        new_playlist_frame.add(new_playlist_panel);
	}
}