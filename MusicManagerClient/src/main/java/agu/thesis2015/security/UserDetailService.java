/**
 * @author: nthienan
 */

package agu.thesis2015.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import agu.thesis2015.domain.User;
import agu.thesis2015.jms.message.Message;
import agu.thesis2015.jms.message.Message.MessageAction;
import agu.thesis2015.jms.message.Message.MessageMethod;
import agu.thesis2015.jms.producer.UserProducer;
import agu.thesis2015.model.RequestData;
import agu.thesis2015.model.Response;
import agu.thesis2015.model.UserDetail;

@Component
public class UserDetailService implements UserDetailsService {

	@Autowired
	private UserProducer producer;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		RequestData requestData = new RequestData();
		requestData.addId(username);
		Message message = new Message(MessageMethod.GET, MessageAction.SECURITY, requestData.toJson());

		User user = null;
		try {
			Response response = producer.sendAndReceive(message);
			user = User.fromJson(response.getResponse().toString());
		} catch (Exception e) {
			return null;
		}
		UserDetail userDetail = new UserDetail(user);
		return userDetail;
	}
}