import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.windows.Win32FullScreenStrategy;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class Test {
	public static void main(String[] args){
		///GUI
		//Create Window
		JFrame f = new JFrame();
		f.setLocation(100,100);
		f.setSize(1000,600);
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
		EmbeddedMediaPlayer emp = mpf.newEmbeddedMediaPlayer(new Win32FullScreenStrategy(f));
		emp.setVideoSurface(mpf.newVideoSurface(c));
		//Full Screen
		//emp.toggleFullScreen();
		//Hide Cursor
		emp.setEnableMouseInputHandling(false);
		//Disable Keyboard
		emp.setEnableKeyInputHandling(false);
		
		String file = "rtsp://184.72.239.149/vod/mp4:BigBuckBunny_175k.mov";
		//Prepare File
		emp.prepareMedia(file);
		//Read the File
		emp.play();
	}
}