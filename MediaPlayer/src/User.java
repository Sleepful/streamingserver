import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.ThreadLocalRandom;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class User {
	
	public List<String> rtsp_list;
	public String current_rtsp;
	public boolean shuffle;
	
	private int current_rtsp_index;
	
	public User() {
		rtsp_list = new ArrayList<String>();
		current_rtsp = "";
		shuffle = false;
		current_rtsp_index = 0;
	}
	
	public void add_rtsp(String rtsp) {
		if(rtsp_list.isEmpty()) {
			current_rtsp = rtsp;
		}
		rtsp_list.add(rtsp);
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
	
}
