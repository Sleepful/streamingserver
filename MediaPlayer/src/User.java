import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import org.zeromq.ZMQ;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class User {
	
	public String name;
	
    public List<String> playlist_names_list;
    public String current_playlist_name;
    public List<List<Integer>> playlist_list;
    public List<Integer> current_playlist;
	public List<String> rtsp_list;
	public String current_rtsp;
	public boolean shuffle;
	
	private int current_rtsp_index;
    private int current_playlist_index;
	
	public User(String name) {
		this.name = name;
		rtsp_list = new ArrayList<String>();
		playlist_names_list = new ArrayList<String>();
		current_playlist_name = "";
		playlist_list = new ArrayList<List<Integer>>();
		current_playlist = new ArrayList<Integer>();
		current_rtsp = "";
		shuffle = false;
		current_rtsp_index = 0;
        current_playlist_index = 0;
	}
	
	public void add_rtsp(String rtsp) {
		if(rtsp_list.isEmpty()) {
			current_rtsp_index = 0;
			current_rtsp = rtsp;
		}
		rtsp_list.add(rtsp);
	}
	
    public void add_playlist(String playlist_name, ArrayList<Integer> playlist, ZMQ.Socket requester) {
        playlist_names_list.add(playlist_name);
        playlist_list.add(playlist);
        if(playlist_names_list.size() == 1) {
            current_playlist_index = 0;
            change_playlist(current_playlist_index,requester);
        }
    }
    
    public void change_playlist(int playlist_index, ZMQ.Socket requester) {
    	String playlist_name = playlist_names_list.get(playlist_index);
    	List<Integer> playlist = playlist_list.get(playlist_index);
        current_playlist_name = playlist_name;
        current_playlist = playlist;
        
        rtsp_list.clear();
        
        byte[] reply = null;
        for(int i=0;i<current_playlist.size();i++){
        	int temp_song_number = current_playlist.get(i);
	        String request = "urls:"+temp_song_number;
	        requester.send(request.getBytes(), 0);
	        reply = requester.recv(0);
	        if(!Objects.equals("end:",new String(reply))) {
	        	String rtsp = new String(reply).replace("\n", "");
	        	add_rtsp(rtsp);
	        }
        }
    }
	
	public void go_to_next_rtsp(EmbeddedMediaPlayer emp) {
		if(shuffle) {
			current_rtsp_index = ThreadLocalRandom.current().nextInt(0, rtsp_list.size());
		}else {
			current_rtsp_index++;
			if(current_rtsp_index >= rtsp_list.size()) {
				current_rtsp_index = 0;
			}
		}
		current_rtsp = rtsp_list.get(current_rtsp_index);
		
		change_rtsp(emp);
	}
	
	public void go_to_index_rtsp(EmbeddedMediaPlayer emp,int index) {
		if(index >= 0 && index < rtsp_list.size()) {
			current_rtsp_index = index;
			current_rtsp = rtsp_list.get(current_rtsp_index);
			change_rtsp(emp);
		}
	}
	
	private void change_rtsp(EmbeddedMediaPlayer emp) {
		emp.stop();
		emp.prepareMedia(current_rtsp);
		emp.play();
	}
	
    public void go_to_next_playlist(ZMQ.Socket requester) {
        current_playlist_index++;
        if(current_playlist_index >= playlist_names_list.size()) {
            current_playlist_index = 0;
        }
        change_playlist(current_playlist_index,requester);
    }
	
}
