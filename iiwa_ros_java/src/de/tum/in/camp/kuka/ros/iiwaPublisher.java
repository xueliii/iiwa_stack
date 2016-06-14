/** Copyright (C) 2015 Salvatore Virga - salvo.virga@tum.de
 * Technische Universitaet Muenchen
 * Chair for Computer Aided Medical Procedures and Augmented Reality
 * Fakultaet fuer Informatik / I16, Boltzmannstrasse 3, 85748 Garching bei Muenchen, Germany
 * http://campar.in.tum.de
 * 
 * LICENSE :
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author Salvatore Virga
 * 
 */

package de.tum.in.camp.kuka.ros;

// ROS imports
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import com.kuka.connectivity.motionModel.smartServo.SmartServo;
import com.kuka.roboticsAPI.deviceModel.LBR;
import com.kuka.roboticsAPI.geometricModel.ObjectFrame;

/**
 * This class provides ROS subscribers for ROS messages defined in the iiwa_msgs ROS package.
 * It allows to send messages of that type via ROS topics named with the following convention :
 * <robot name>/state/<iiwa message type> (e.g. MyIIWA/state/CartesianPosition)
 */
public class iiwaPublisher extends AbstractNodeMain {

	// ROSJava Publishers for iiwa_msgs
	// Cartesian Message Publishers
	private Publisher<geometry_msgs.PoseStamped> cartesianPosePublisher;
//	private Publisher<iiwa_msgs.CartesianVelocity> cartesianVelocityPublisher;
	private Publisher<geometry_msgs.WrenchStamped> cartesianWrenchPublisher;
	// Joint Message Publishers
	private Publisher<iiwa_msgs.JointPosition> jointPositionPublisher;
	private Publisher<iiwa_msgs.JointStiffness> jointStiffnessPublisher;
	private Publisher<iiwa_msgs.JointDamping> jointDampingPublisher;
	private Publisher<iiwa_msgs.JointTorque> jointTorquePublisher;
//	private Publisher<iiwa_msgs.JointVelocity> jointVelocityPublisher;
	// UserKey Event Publisher
	private Publisher<std_msgs.String> iiwaButtonPublisher; // TODO: iiwa_msgs.ButtonEvent
	// JointState publisher (optional)
	private Publisher<sensor_msgs.JointState> jointStatesPublisher;
	private boolean publishJointState = false;
	
	// Local iiwa_msgs to publish 
	// Cartesian Messages
	private geometry_msgs.PoseStamped cp;
//	private iiwa_msgs.CartesianVelocity cv;
	private geometry_msgs.WrenchStamped cw;
	// Joint Messages
	private iiwa_msgs.JointPosition jp;
	private iiwa_msgs.JointStiffness js;
	private iiwa_msgs.JointDamping jd;
	private iiwa_msgs.JointTorque jt;
//	private iiwa_msgs.JointVelocity jv;
	private sensor_msgs.JointState joint_states;

	// Object to easily build iiwa_msgs from the current robot state
	private iiwaMessageGenerator helper = new iiwaMessageGenerator();

	// Name to use to build the name of the ROS topics
	private String iiwaName = "iiwa";

	/**
	 * Constructs a series of ROS publishers for messages defined by the iiwa_msgs ROS package. <p>
	 * The initial values of the messages are all set to zeros.
	 */
	public iiwaPublisher(String robotName) {
		cp = cartesianPosePublisher.newMessage();
//		cv = cartesianVelocityPublisher.newMessage();
		cw = cartesianWrenchPublisher.newMessage();

		jp = jointPositionPublisher.newMessage();
		js = jointStiffnessPublisher.newMessage();
		jd = jointDampingPublisher.newMessage();
		jt = jointTorquePublisher.newMessage();
//		jv = jointVelocityPublisher.newMessage();
		
		iiwaName = robotName;
	}

	/**
	 * Constructs a series of ROS publishers for messages defined by the iiwa_msgs ROS package. <p>
	 * The initial values of the messages are set using the current state of the iiwa robot passed.
	 * Except for Velocity messages, those will be initialized to zero.<br>
	 * For Cartesian messages, the initial values will refer to the frame of the robot's Flange.
	 * @param robot : an iiwa Robot, its current state is used to set up initial values for the messages.
	 */
	public iiwaPublisher(LBR robot, String robotName) {
		cp = helper.buildCartesianPose(robot);
//		cv = helper.buildCartesianVelocity(robot);
		cw = helper.buildCartesianWrench(robot);

		jp = helper.buildJointPosition(robot);
		js = helper.buildJointStiffness(robot, null);
		jt = helper.buildJointTorque(robot);
//		jv = helper.buildJointVelocity(robot);
		
		iiwaName = robotName;
	}

	/**
	 * Constructs a series of ROS publishers for messages defined by the iiwa_msgs package. <p>
	 * The initial values of the messages are set using the current state of the iiwa robot passed.
	 * Except for Velocity messages, those will be initialized to zero.<br>
	 * For Cartesian messages, the initial values will refer to the given frame.
	 * @param robot : an iiwa Robot, its current state is used to set up initial values for the messages.
	 * @param frame : reference frame to use to set up initial values for Cartesian messages.
	 */
	public iiwaPublisher(LBR robot, ObjectFrame frame, String robotName) {
		cp = helper.buildCartesianPose(robot, frame);
//		cv = helper.buildCartesianVelocity(robot, frame);
		cw = helper.buildCartesianWrench(robot, frame);

		jp = helper.buildJointPosition(robot);
		js = helper.buildJointStiffness(robot, null);
		jt = helper.buildJointTorque(robot);
//		jv = helper.buildJointVelocity(robot);
		
		iiwaName = robotName;
	}

	/**
	 * Set the CartesianPosition message to be published.<p>
	 * @param pose : CartesianPosition message to publish.
	 */
	public void setCartesianPose(geometry_msgs.PoseStamped pose) {
		cp = pose;
	}
	/**
	 * Set the CartesianVelocity message to be published.<p>
	 * @param velocity : CartesianVelocity message to publish.
	 */
//	public void setCartesianVelocity(iiwa_msgs.CartesianVelocity velocity) {
//		cv = velocity;
//	}
	/**
	 * Set the CartesianWrench message to be published.<p>
	 * @param wrench : CartesianWrench message to publish.
	 */
	public void setCartesianWrench(geometry_msgs.WrenchStamped wrench) {
		cw = wrench;
	}
	/**
	 * Set the JointPosition message to be published.<p>
	 * @param position : JointPosition message to publish.
	 */
	public void setJointPosition(iiwa_msgs.JointPosition position) {
		jp = position;
	}
	/**
	 * Set the JointStiffness message to be published.<p>
	 * @param position : JointStiffness message to publish.
	 */
	public void setJointStiffness(iiwa_msgs.JointStiffness stiffness) {
		js = stiffness;
	}
	/**
	 * Set the JointDamping message to be published.<p>
	 * @param position : JointDamping message to publish.
	 */
	public void setJointDamping(iiwa_msgs.JointDamping damping) {
		jd = damping;
	}
	/**
	 * Set the JointTorque message to be published.<p>
	 * @param torque : JointTorque message to publish.
	 */
	public void setJointTorque(iiwa_msgs.JointTorque torque) {
		jt = torque;
	}
	/**
	 * Set the JointVelocity message to be published.<p>
	 * @param velocity : JointVelocity message to publish.
	 */
//	public void setJointVelocity(iiwa_msgs.JointVelocity velocity) {
//		jv = velocity;
//	}
	
	public void setJointState(sensor_msgs.JointState state) {
		joint_states = state;
	}
	
	public void setPublishJointStates(boolean b) {
		publishJointState = b;
	}

	/**
	 * Set the name to use to compose the ROS topics' names for the publishers. <p>
	 * e.g. setIIWAName("dummy"), the topics' names will be "dummy/state/...". <br>
	 * The creation of the nodes is performed when the <i>execute</i> method from a <i>nodeMainExecutor</i> is called.
	 * @param newName : the new name to use for ROS topics.
	 */
	public void setIIWAName(String newName) {
		iiwaName = newName;
	}

	/**
	 * Returns the current name used to compose the ROS topics' names for the publishers. <p>
	 * e.g. returning "dummy" means that the topics' names will be "dummy/state/...". <br>
	 * The creation of the nodes is performed when the <i>execute</i> method from a <i>nodeMainExecutor</i> is called.
	 * @return the current name to use for ROS topics.
	 */
	public String getIIWAName() {
		return iiwaName;
	}

	/**
	 * @see org.ros.node.NodeMain#getDefaultNodeName()
	 */
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of(iiwaName + "/publisher");
	}

	/**
	 * This method is called when the <i>execute</i> method from a <i>nodeMainExecutor</i> is called.<br>
	 * Do <b>NOT</b> manually call this. <p> 
	 * @see org.ros.node.AbstractNodeMain#onStart(org.ros.node.ConnectedNode)
	 */
	@Override
	public void onStart(final ConnectedNode connectedNode) {
		cartesianPosePublisher = connectedNode.newPublisher(iiwaName + "/state/CartesianPose", geometry_msgs.PoseStamped._TYPE);
//		cartesianVelocityPublisher = connectedNode.newPublisher(iiwaName + "/state/CartesianVelocity", iiwa_msgs.CartesianVelocity._TYPE);
		cartesianWrenchPublisher = connectedNode.newPublisher(iiwaName + "/state/CartesianWrench", geometry_msgs.WrenchStamped._TYPE);

		jointPositionPublisher = connectedNode.newPublisher(iiwaName + "/state/JointPosition", iiwa_msgs.JointPosition._TYPE);
		jointStiffnessPublisher = connectedNode.newPublisher(iiwaName + "/state/JointStiffness", iiwa_msgs.JointStiffness._TYPE);
		jointDampingPublisher = connectedNode.newPublisher(iiwaName + "/state/JointDamping", iiwa_msgs.JointDamping._TYPE);
		jointTorquePublisher = connectedNode.newPublisher(iiwaName + "/state/JointTorque", iiwa_msgs.JointTorque._TYPE);
//		jointVelocityPublisher = connectedNode.newPublisher(iiwaName + "/state/JointVelocity", iiwa_msgs.JointVelocity._TYPE);
		
		iiwaButtonPublisher = connectedNode.newPublisher(iiwaName + "/state/buttonEvent", std_msgs.String._TYPE);
		jointStatesPublisher = connectedNode.newPublisher(iiwaName + "/joint_states", sensor_msgs.JointState._TYPE);
	}
	
	public void publishCurrentState(LBR robot, SmartServo motion) throws InterruptedException {
		setCartesianPose(helper.buildCartesianPose(robot));
		setCartesianWrench(helper.buildCartesianWrench(robot));
		
		setJointPosition(helper.buildJointPosition(robot));
		setJointTorque(helper.buildJointTorque(robot));
		setJointStiffness(helper.buildJointStiffness(robot, motion));
		setJointDamping(helper.buildJointDamping(robot, motion));
		setJointState(helper.buildJointState(robot, motion));
		publish();
	}
	
	public void publishCurrentState(LBR robot, SmartServo motion, ObjectFrame frame) throws InterruptedException {
		setCartesianPose(helper.buildCartesianPose(robot, frame));
		setCartesianWrench(helper.buildCartesianWrench(robot, frame));
		
		setJointPosition(helper.buildJointPosition(robot));
		setJointTorque(helper.buildJointTorque(robot));
		setJointStiffness(helper.buildJointStiffness(robot, motion));
		setJointDamping(helper.buildJointDamping(robot, motion));
		setJointState(helper.buildJointState(robot, motion));
		publish();
	}

	/**
	 * Publishes to the respective topics all the iiwa_msgs with the values they are currently set to.<p>
	 * To set the messages to be published use their set methods.<br>
	 * Only the nodes that currently have subscribers will publish the messages.<br>
	 * @throws InterruptedException
	 */
	public void publish() throws InterruptedException {
		/*
		 * For each message, it's checked whether there is a subscriber waiting for a message
		 * or not, if so the message is published.
		 */
		publishIfSubscriber(cartesianPosePublisher, cp);
//		publishIfSubscriber(cartesianVelocityPublisher, cv);
		publishIfSubscriber(cartesianWrenchPublisher, cw);
		publishIfSubscriber(jointPositionPublisher, jp);
		publishIfSubscriber(jointStiffnessPublisher, js);
		publishIfSubscriber(jointDampingPublisher, jd);
		publishIfSubscriber(jointTorquePublisher, jt);
//		publishIfSubscriber(jointVelocityPublisher, jv);
		if (publishJointState)
			publishIfSubscriber(jointStatesPublisher, joint_states);
	}

	<T> void publishIfSubscriber(Publisher<T> p, T m) {
		if (p != null && m != null) {
			if (p.getNumberOfSubscribers() > 0)
				p.publish(m);
		}
	}
	
	public void publishButtonPressed(String name) {
		final std_msgs.String msg = iiwaButtonPublisher.newMessage();
		msg.setData(name + "_pressed");
		iiwaButtonPublisher.publish(msg);
	}
	
	public void publishButtonReleased(String name) {
		final std_msgs.String msg = iiwaButtonPublisher.newMessage();
		msg.setData(name + "_released");
		iiwaButtonPublisher.publish(msg);
	}
}
