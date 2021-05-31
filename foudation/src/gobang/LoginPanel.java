package gobang;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Time;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import java.awt.Font;

/**
 * 登录面板
 */
public class LoginPanel extends javax.swing.JPanel {

	private Socket socket;// 客户端套接字
	private UserBean user;// 本地创建的用户
	private javax.swing.JButton closeButton;// 关闭按钮
	private javax.swing.JTextField ipTextField;// IP输入框
	private javax.swing.JLabel nameLabel;// 昵称标签
	private javax.swing.JLabel ipLabel;// IP标签
	private javax.swing.JButton loginButton;// 登陆按钮
	private javax.swing.JTextField nameTextField;// 用户名输入框
	private javax.swing.JButton machineButton;// 人机对战按钮
	public static boolean isManMachineWar;// 是否为人机对战

	/**
	 * 构造方法
	 */
	public LoginPanel() {
		initComponents(); // 调用初始化界面的方法
	}

	/**
	 * 初始化登录界面的方法
	 */
	private void initComponents() {
		// 创建表格包布局约束对象，用于微调组件在表格包布局的位置
		java.awt.GridBagConstraints gridBagConstraints;
		nameLabel = new javax.swing.JLabel();// 初始化昵称标签
		nameTextField = new javax.swing.JTextField();// 初始化昵称输入框
		ipLabel = new javax.swing.JLabel();// 初始化“对方IP”标签
		ipTextField = new javax.swing.JTextField();// 初始化ip输入框
		loginButton = new javax.swing.JButton();// 初始化登陆按钮
		closeButton = new javax.swing.JButton();// 初始化关闭按钮
		machineButton = new javax.swing.JButton();// 初始化人机对战按钮
		setForeground(java.awt.Color.gray);// 前景色为灰色
		setOpaque(false);// 可以是透明的
		setLayout(new java.awt.GridBagLayout());// 使用网格包布局

		nameLabel.setFont(new Font("隶书", Font.ITALIC, 24));// 设定字体
		nameLabel.setForeground(new java.awt.Color(255, 255, 255));// 设定颜色
		nameLabel.setText("昵   称：");// 设定文字内容
		gridBagConstraints = new java.awt.GridBagConstraints();// 初始化表格包布局约束对象
		gridBagConstraints.gridx = 0;// 处于第一行
		gridBagConstraints.gridy = 0;// 处于第一列
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;// 右对齐
		add(nameLabel, gridBagConstraints);// 将组件添加到指定位置

		gridBagConstraints = new java.awt.GridBagConstraints();// 初始化表格包布局约束对象
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;// 水平调整大小
		gridBagConstraints.ipady = -5;// 上下填充距离
		gridBagConstraints.gridwidth = 2;// 一行占两个格子那么宽
		// 间距类，顶部间距3，左边间距0，底部间距3，右边间距0
		gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
		add(nameTextField, gridBagConstraints);// 将组件添加到指定位置

		ipLabel.setFont(new Font("隶书", Font.ITALIC, 24));
		ipLabel.setForeground(java.awt.Color.white);
		ipLabel.setText("对方 IP：");
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		add(ipLabel, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.ipady = -5;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
		add(ipTextField, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new Insets(0, 0, 0, 40);
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		add(machineButton, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new Insets(0, 0, 0, 40);
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		add(loginButton, gridBagConstraints);

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new Insets(0, 0, 0, 55);
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 2;
		add(closeButton, gridBagConstraints);

		addMouseListener(new java.awt.event.MouseAdapter() {// 添加鼠标事件
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				formMouseClicked(evt);// 在其他位置点鼠标
			}
		});

		machineButton.setText("人机对战");
		machineButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				machinButtonActionPerformed();
			}
		});

		loginButton.setText("登录");
		loginButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				loginButtonActionPerformed(evt);
			}
		});
		closeButton.setText("关闭");
		closeButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				closeButtonActionPerformed(evt);// 关闭按钮触发程序关闭
			}
		});
	}

	/**
	 * 关闭按钮触发程序关闭
	 *
	 * @param evt
	 */
	private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {
		System.exit(0);// 整个程序终止运行
	}

	/**
	 * 当鼠标点击空白位置时触发的方法
	 *
	 * @param evt
	 */
	private void formMouseClicked(java.awt.event.MouseEvent evt) {
		JOptionPane.showMessageDialog(this, "还没登录呢，往哪点？");// 弹出提示框
	}

	/**
	 * 登录按钮的事件处理方法
	 *
	 * @param evt
	 *            - 按钮的事件对象
	 */
	private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			// 获取主窗体的实例对象
			MainFrame mainFrame = (MainFrame) getParent().getParent();
			String name = nameTextField.getText(); // 获取用户昵称
			if (name.trim().isEmpty()) {// 如果名称没有任何可显示的字符
				JOptionPane.showMessageDialog(this, "请输入昵称");
				return;
			}
			String ipText = ipTextField.getText(); // 获取对家IP地址
			if (ipText == null || ipText.isEmpty()) {// 如果没有输入任何IP地址
				JOptionPane.showMessageDialog(this, "请输入对家IP地址");
				return;
			}
			ipTextField.setEditable(true);// IP地址输入框可被编辑
			InetAddress ip = InetAddress.getByName(ipText);// 获取改地址对象
			if (ip.equals(InetAddress.getLocalHost())) {// 如果这个地址与我本机地址相同
				JOptionPane.showMessageDialog(this, "不能输入自己的IP地址");// 提示不可以输入自己的IP地址
				return;// 方法终止
			}
			socket = new Socket(ip, 9527); // 创建Socket连接对家主机
			if (socket.isConnected()) { // 如果连接成功
				user = new UserBean(); // 创建用户对象
				Time time = new Time(System.currentTimeMillis()); // 获取当前时间对象
				user.setName(name); // 初始化用户昵称
				user.setHost(InetAddress.getLocalHost()); // 初始化用户IP
				user.setTime(time); // 初始化用户登录时间
				socket.setOOBInline(true); // 启用紧急数据的接收
				mainFrame.setSocket(socket); // 设置主窗体的Socket连接对象
				mainFrame.setUser(user); // 添加本地用户对象到主窗体对象
				mainFrame.send(user); // 发送本地用户对象到对家主机
				isManMachineWar = false;// 标记此局为玩家对战
				setVisible(false); // 隐藏登录窗体
			}
		} catch (UnknownHostException ex) {
			// 将当前异常记录为Level.SEVERE（最高级别）日志，日志名叫LoginPanel.class.getName()
			Logger.getLogger(LoginPanel.class.getName()).log(Level.SEVERE,
					null, ex);
			JOptionPane.showMessageDialog(this, "输入的IP不正确");
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "对方主机无法连接");
			e.printStackTrace();
		}
	}

	/**
	 * 人机对战事件处理方法
	 */
	private void machinButtonActionPerformed() {
		MainFrame mainFrame = (MainFrame) getParent().getParent();// 获取主窗体的实例对象
		String name = nameTextField.getText(); // 获取用户昵称
		if (name.trim().isEmpty()) {// 如果用户没有输入名称
			JOptionPane.showMessageDialog(this, "请输入昵称");// 弹出对话框
			return;
		}
		try {
			socket = new Socket("127.0.0.1", 9527);// 创建连接本地的套接字
			if (socket.isConnected()) { // 如果连接成功
				user = new UserBean(); // 创建用户对象
				// 获取当前时间对象
				Time time = new Time(System.currentTimeMillis());
				user.setName(name); // 初始化用户昵称
				user.setHost(InetAddress.getLocalHost()); // 初始化用户IP
				user.setTime(time); // 初始化用户登录时间
				socket.setOOBInline(true); // 启用紧急数据的接收
				mainFrame.setSocket(socket); // 设置主窗体的Socket连接对象
				UserBean machine = new UserBean();// 创建AI机器人
				machine.setName("明日机器人");// 为机器人设置名称
				machine.setHost(InetAddress.getLocalHost()); // 初始化机器人IP
				machine.setTime(new Time(0));// 设置时间设为一个较大的，确保机器人选黑子
				mainFrame.setUser(user); // 将玩家放在窗体左边
				mainFrame.setTowardsUser(machine); // 将机器人放在主窗体右边
				mainFrame.setSendButtonEnable(false);// 人机对战取消发送消息功能
				isManMachineWar = true;// 标记此局为人机对战
				setVisible(false); // 隐藏登录窗体
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 绘制组件界面的方法
	 *
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g; // 获取2D绘图上下文
		Composite composite = g2.getComposite(); // 备份合成模式
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				0.9f)); // 设置绘图使用透明合成规则
		g2.fillRect(0, 0, getWidth(), getHeight()); // 使用当前颜色填充矩形空间
		g2.setComposite(composite); // 恢复原有合成模式
		super.paintComponent(g2); // 执行超类的组件绘制方法
	}

	void setLinkIp(String ip) {
		ipTextField.setText(ip);// IP输入框写入指定内容
		ipTextField.setEditable(false);// IP输入框不可编辑
		nameTextField.requestFocus();// 名称输入框获得记得焦点
	}
}
