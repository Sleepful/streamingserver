import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.ThreadLocalRandom;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class User {
	
	public List<String> playlists_list;
	public String current_playlist;
	public List<String> rtsp_list;
	public String current_rtsp;
	public boolean shuffle;
	
	private int current_rtsp_index;
	private int current_playlist_index;
	
	public User() {
		rtsp_list = new ArrayList<String>();
		playlists_list = new ArrayList<String>();
		current_rtsp = "";
		current_playlist = "";
		shuffle = false;
		current_rtsp_index = 0;
		current_playlist_index = 0;
	}
	
	public void add_rtsp(String rtsp) {
		if(rtsp_list.isEmpty()) {
			current_rtsp = rtsp;
		}
		rtsp_list.add(rtsp);
	}
	
	public void add_playlist(String playlist) {
		if(playlists_list.isEmpty()) {
			current_playlist = playlist;
		}
		playlists_list.add(playlist);
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
	
	public void go_to_next_playlist() {
		current_playlist_index++;
		if(current_playlist_index >= playlists_list.size()) {
			current_playlist_index = 0;
		}
		current_playlist = playlists_list.get(current_playlist_index);
	}
	
}
