package gobang;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 电脑智能对战算法
 *
 * @author shenxiaoqi
 *
 */
public class AI {
	MainFrame frame;// 主窗体对象
	private GobangModel gobangModel1;// 棋盘模型类
	private GobangPanel gobangPanel1;// 棋盘面板类
	private final int boundary = 8;// 棋盘边界值常量，用于捕捉棋型时填充边界
	private int alpha=0; //剪枝用的
	private int beta=0; //剪枝用的
	private byte[][] chessmanArray;
	private ArrayList<chess_ai> eplist;

	public AI(MainFrame outer) {
		frame = outer;// 外部窗体对象
		gobangModel1 = GobangModel.getInstance();// 获取棋子模型
		gobangPanel1 = frame.getChessPanel1().getGobangPanel1();// 获得当前使用的棋盘面板
		chessmanArray = gobangModel1.getChessmanArray();// 获得棋盘
	}

	/**
	 * AI处理玩家命令的方法
	 *
	 * @param messageObj
	 *            - 命令代码
	 */
	public void oprationHandler(Object messageObj) {
		int code = (Integer) messageObj; // 获取命令代码
		switch (code) {// 判断命令
			case ChessPanel.OPRATION_START_MACHINE: // 如果是玩家请求开始游戏
				frame.getChessPanel1().setTowardsStart(true); // 设置AI的游戏开始状态为true
				break;
			default:
				// System.out.println("操作代码：" + code);
		}
	}

	/**
	 * 判断一个位置周围有没有子，这是用来决定哪些地方适合下的
	 *
	 * @return boolean
	 *
	 */
	public boolean hasnext(int x,int y){
		chessmanArray = gobangModel1.getChessmanArray();// 获得棋盘
		int a,b;
		if(x-3>=0){
			a=x-3;
		}
		else{
			a=0;
		}
		if(y-3>=0){
			b=y-3;
		}
		else{
			b=0;
		}
		for(int  i=a;i<x+3&&i<15;i++){
			for(int j=b;j<y+3&&j<15;j++){
				if(chessmanArray[i][j]==1||chessmanArray[i][j]==-1){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 生成基于hasnext()方法的空子序列，也就是可以选择的点
	 *
	 * @return ArrayList<chess_ai>
	 *
	 */

	public ArrayList<chess_ai> creatempty_list()//产生空子序列
	{
		ArrayList<chess_ai> emptylist=new ArrayList<chess_ai>();
		chessmanArray = gobangModel1.getChessmanArray();// 获得棋盘
		int i,j;
		for( i=0;i<15;i++){
			for(j=0;j<15;j++){
				if(chessmanArray[i][j]!=GobangModel.WHITE_CHESSMAN&&chessmanArray[i][j]!=GobangModel.BLACK_CHESSMAN&&hasnext(i,j))
				{
					chess_ai Chess_ai= new chess_ai(i,j);
					emptylist.add(Chess_ai);
				}
			}
		}
		System.out.println("集合的长度：" + emptylist.size());
		return emptylist;
	}


	/**
	 * 一个简单的权值估测方法，为不同类型的棋形记不同分数
	 *
	 * @input
	 * number:连接的棋子数量 type：“死活"
	 * @return int 棋形分数
	 *
	 */

	public int getscore(int number,int type)//计分权值表
	{
		if(number>=5)	return 100000;
		else if(number==4)
		{
			if(type==2)	return 10000;
			else if(type==1)	return 1000;
		}
		else if(number==3)
		{
			if(type==2)	return 1000;
			else if(type==1)	return 100;
		}
		else if(number==2)
		{
			if(type==2)	return 100;
			else if(type==1)	return 10;
		}
		else if(number==1&&type==2)	return 10;
		return 0;
	}

	/**
	 * 把输入的一维数组格式的行列等拆解，然后调用计分权值表给出这一个一维数组的估值
	 *
	 * @input
	 * n:包含某一个一位数组上棋子的样子 color：预期棋子颜色
	 * @return int 该一维数组的分数
	 *
	 */
	public int countscore(ArrayList<Integer> n,int color)
	{
		chessmanArray = gobangModel1.getChessmanArray();// 获得棋盘
		int scoretmp=0;
		int len=n.size();
		int empty1=0;
		int number=0;
		if(n.get(0)==0)	++empty1;
		else if(n.get(0)==color)	++number;
		int i=1;
		while(i<len)
		{
			if(n.get(i)==color)	++number;
			else if(n.get(i)==0)
			{
				if(number==0)	empty1=1;
				else
				{
					scoretmp+=getscore(number,empty1+1);
					empty1=1;
					number=0;
				}
			}
			else
			{
				scoretmp+=getscore(number,empty1);
				empty1=0;
				number=0;
			}
			++i;
		}
		scoretmp+=getscore(number,empty1);
		return scoretmp;
	}

	/**
	 * 评估函数，预测局势，本质是多个方向拆解出一维数组再调用countscore（）
	 *
	 * @input 无
	 *
	 * @return int 最终全局估值
	 *
	 */

	public int evaluate() {
		chessmanArray = gobangModel1.getChessmanArray();// 获得棋盘
		int scorecomputer = 0;
		int scorehumman = 0;
		//-
		for (int i = 0; i < 15; i++) {
			ArrayList<Integer> nt = new ArrayList<Integer>();
			for (int j = 0; j < 15; j++) {
				if(chessmanArray[i][j]==GobangModel.WHITE_CHESSMAN){
					nt.add(2);
				}
				else if(chessmanArray[i][j]==GobangModel.BLACK_CHESSMAN){
					nt.add(1);
				}
				else{
					nt.add(0);
				}
			}
			scorecomputer += countscore(nt, 2);
			scorehumman+= countscore(nt, 1);
			nt.clear();
		}
		//|
		for (int j = 0; j < 15; j++) {
			ArrayList<Integer> nt = new ArrayList<Integer>();
			for (int i = 0; i < 15; i++) {
				if(chessmanArray[i][j]==GobangModel.WHITE_CHESSMAN){
					nt.add(2);
				}
				else if(chessmanArray[i][j]==GobangModel.BLACK_CHESSMAN){
					nt.add(1);
				}
				else{
					nt.add(0);
				}
			}
			scorecomputer += countscore(nt, 2);
			scorehumman+= countscore(nt, 1);
			nt.clear();
		}
		//上半正斜线
		for (int i = 0; i < 15; i++) {
			ArrayList<Integer> nt = new ArrayList<Integer>();
			int x, y;
			for (x = i, y = 0; x < 15 && y < 15; x++, y++){
				if(chessmanArray[y][x]==GobangModel.WHITE_CHESSMAN){
					nt.add(2);
				}
				else if(chessmanArray[y][x]==GobangModel.BLACK_CHESSMAN){
					nt.add(1);
				}
				else{
					nt.add(0);
				}
			}
			scorecomputer += countscore(nt, 2);
			scorehumman+= countscore(nt, 1);
			nt.clear();
		}
		//下半正斜线
		for (int j = 0; j < 15; j++) {
			int x, y;
			ArrayList<Integer> nt = new ArrayList<Integer>();
			for (x = 0, y = j; y < 15 && x < 15; x++, y++){
				if(chessmanArray[y][x]==GobangModel.WHITE_CHESSMAN){
					nt.add(2);
				}
				else if(chessmanArray[y][x]==GobangModel.BLACK_CHESSMAN){
					nt.add(1);
				}
				else{
					nt.add(0);
				}
			}
			scorecomputer += countscore(nt, 2);
			scorehumman+= countscore(nt, 1);
			nt.clear();
		}
		//上半反斜线
		for (int i = 0; i < 15; i++) {
			ArrayList<Integer> nt = new ArrayList<Integer>();
			int x, y;
			for (y = i, x = 0; y >= 0 && x < 15; y--, x++){
				if(chessmanArray[y][x]==GobangModel.WHITE_CHESSMAN){
					nt.add(2);
				}
				else if(chessmanArray[y][x]==GobangModel.BLACK_CHESSMAN){
					nt.add(1);
				}
				else{
					nt.add(0);
				}
			}
			scorecomputer += countscore(nt, 2);
			scorehumman+= countscore(nt, 1);
			nt.clear();
		}
		//下半反斜线
		for (int j = 0; j < 15; j++) {
			ArrayList<Integer> nt = new ArrayList<Integer>();
			int x, y;
			for (y = j, x = 14; y < 15 && x >= 0; y++, x--){
				if(chessmanArray[x][y]==GobangModel.WHITE_CHESSMAN){
					nt.add(2);
				}
				else if(chessmanArray[x][y]==GobangModel.BLACK_CHESSMAN){
					nt.add(1);
				}
				else{
					nt.add(0);
				}
			}
			scorecomputer += countscore(nt, 2);
			scorehumman+= countscore(nt, 1);
			nt.clear();
		}
		return scorehumman-scorecomputer;
	}

	/**
	 * 递归计算人权值树
	 *
	 * @input 深度
	 *
	 * @return int 最终全局估值
	 *
	 */

	public int min_noalphabeta(int depth)//玩家落子时													//当min（人）走步时，人的最好情况
	{
		chessmanArray = gobangModel1.getChessmanArray();// 获得棋盘
		int res=evaluate();
		if(depth<=0)
		{
			return res;
		}
		ArrayList<chess_ai>  v;
		v=creatempty_list();
		int len=v.size();
		int best=212121121;
		for(int i=0;i<len;++i)
		{
			chessmanArray[v.get(i).getX()][v.get(i).getY()] = GobangModel.WHITE_CHESSMAN;// 在此处下一枚白棋
			gobangModel1.setChessmanArray(chessmanArray);// 更新棋盘数据
			int tmp=max_noalphabeta(depth-1);
			if(tmp<best)	best=tmp;//玩家落子时选择最有利自己的局面，将推迟，叶子节点做出选择后，层层往上推
			alpha = Math.max(beta, tmp);
			chessmanArray[v.get(i).getX()][v.get(i).getY()] = 0;
			gobangModel1.setChessmanArray(chessmanArray);// 更新棋盘数据
			if(alpha<=tmp){
				break;              //剪枝
			}
		}
		return best;
	}

	/**
	 * 递归计算AI权值树
	 *
	 * @input 深度
	 *
	 * @return int 最终全局估值
	 *
	 */

	int max_noalphabeta(int depth)													//当max（电脑）走步时，max（电脑）应该考虑最好的情况
	{
		chessmanArray = gobangModel1.getChessmanArray();// 获得棋盘
		int res=evaluate();
		if(depth<=0)
		{
			return res;
		}
		ArrayList<chess_ai>  v;
		v=creatempty_list();
		int len=v.size();
		int best=-22222222;
		for(int i=0;i<len;++i)
		{
			chessmanArray[v.get(i).getX()][v.get(i).getY()] = GobangModel.BLACK_CHESSMAN;// 在此处下一枚黑棋
			gobangModel1.setChessmanArray(chessmanArray);// 更新棋盘数据
			int tmp=min_noalphabeta(depth-1);
			if(tmp>best)	best=tmp;//电脑落子时，选择最有利于自己的局面，将推迟
			alpha = Math.max(best, alpha);
			chessmanArray[v.get(i).getX()][v.get(i).getY()] = 0;
			gobangModel1.setChessmanArray(chessmanArray);// 更新棋盘数据
			if(beta>=tmp){
				break;              //剪枝
			}
		}
		return best;
	}


	/**
	 * 最终的最佳落点
	*/
	public int[] predict_perfect_spot(int depth)//极大极小值算法搜索n步后的最优解
	{
		chessmanArray = gobangModel1.getChessmanArray();// 获得棋盘
		ArrayList<chess_ai>  v;
		v=creatempty_list();
		int best=-22222222;
		int len=v.size();
		int best_pair=0;
		for(int i=0;i<len;++i)
		{
			chessmanArray = gobangModel1.getChessmanArray();// 获得棋盘
			chessmanArray[v.get(i).getX()][v.get(i).getY()] = GobangModel.BLACK_CHESSMAN;// 在此处下一枚黑棋
			gobangModel1.setChessmanArray(chessmanArray);// 更新棋盘数据
			int tmp=min_noalphabeta(depth-1);
			if(tmp>best)
			{
				best=tmp;
				best_pair=i;
			}
			chessmanArray = gobangModel1.getChessmanArray();// 获得棋盘
			chessmanArray[v.get(i).getX()][v.get(i).getY()] = 0;// 在此处下一枚黑棋
			gobangModel1.setChessmanArray(chessmanArray);// 更新棋盘数据
		}
		return new int[] {v.get(best_pair).getX(),v.get(best_pair).getY()};
	}

	/**
	 * 电脑落棋
	 */
	public void chess() {
		//int chessIndex[] = forEach();// 获取AI判断的下棋位置
		int chessIndex[]=predict_perfect_spot(3);
		int key=evaluate();
		System.out.println(key);
		eplist=creatempty_list();
		gobangPanel1.chessForMachine(chessIndex[0], chessIndex[1]);// 将棋子放入棋盘指定位置
	}

	/**
	 * 遍历棋盘棋子位置，寻找落棋点。 查看每一个棋子所能形成的棋型，找出其中威胁最大的，根据棋型库给出的破解位置找出落棋点。
	 *
	 * @return 落棋点坐标数组
	 */
	private int[] forEach() {
		int x = -1, y = -1;// 将要下的棋子坐标
		int threat = 0;// 棋盘上出现的最大威胁值
		chessmanArray = gobangModel1.getChessmanArray();
		for (int i = 0; i < 15; i++) {// 遍历棋盘行
			for (int j = 0; j < 15; j++) {// 遍历棋盘列
				if (chessmanArray[i][j] > 0) {// 如果此处有白棋子
					int tmp[] = catchChessModle(i, j, chessmanArray);// 捕捉每个棋子形成的棋型
					if (tmp[0] > threat) {// 如果存在比当前最大威胁值还要大的威胁值，则记录此处落子坐标
						threat = tmp[0];// 更新最大威胁值
						x = tmp[1];// 更新落子横坐标
						y = tmp[2];// 更新落子纵坐标
					}
				}
			}
		}
		return new int[] { x, y };// 返回横纵坐标组成的一维数组
	}

	/**
	 * 捕捉棋牌中某一个棋子所能产生的最大威胁值及破解下法。
	 * 以一枚棋子为原点，获取其四个方向的连线上所有的棋子，分别判断这四条线上可能产生的有威胁的棋型，
	 * 根据棋型库给出的破解位置，换算成棋盘上的位置，并返回成结果数组。
	 *
	 * @param x
	 *            -被捕捉的棋子横坐标
	 * @param y
	 *            -被捕捉的棋子纵坐标
	 * @param chessmanArray
	 *            - 棋盘数组
	 * @return 返回一维数组，数值分别代表{最大威胁值,破解棋子的横坐标,破解棋子的纵坐标}
	 */
	private int[] catchChessModle(int x, int y, byte[][] chessmanArray) {

		// 索引0：记录此位置的棋子可产生的最大威胁值或优势值
		// 索引1：对应下棋横坐标
		// 索引2：对应下棋横坐标
		int position[] = new int[3];

		// 创建以被捕捉棋子为中心的四个方向形成的棋型
		// 以参数点为中心点，保存四个方向的棋型，方向分别为 — | \ /
		int model[][] = new int[4][11];
		for (int tmp[] : model) {// 遍历数组
			Arrays.fill(tmp, boundary);// 将数组填充为边界常量
		}
		// 把参数点放入每行的中心部位
		model[0][5] = model[1][5] = model[2][5] = model[3][5] = chessmanArray[x][y];
		// 以该棋子为中心，向两边走5步
		for (int i = 1; i <= 5; i++) {
			// 水平方向棋型
			if (x - i >= 0) {// 如果没有走出边界
				model[0][5 - i] = chessmanArray[x - i][y];// 将左侧棋子记录到水平棋型当中
			}
			if (x + i <= 14) {// 如果没有走出边界
				model[0][5 + i] = chessmanArray[x + i][y];// 将右侧棋子记录到水平棋型当中
			}

			// 垂直方向棋型
			if (y - i >= 0) {// 如果没有走出边界
				model[1][5 - i] = chessmanArray[x][y - i];// 将上方棋子记录到垂直棋型当中
			}
			if (y + i <= 14) {// 如果没有走出边界
				model[1][5 + i] = chessmanArray[x][y + i];// 将下方棋子记录到垂直棋型当中
			}

			// 反斜杠方向棋型
			if (x - i >= 0 && y + i <= 14) {// 如果没有走出边界
				model[2][5 - i] = chessmanArray[x - i][y + i];// 将左下方棋子记录到反斜棋型当中
			}
			if (x + i <= 14 && y - i >= 0) {// 如果没有走出边界
				model[2][5 + i] = chessmanArray[x + i][y - i];// 将右上方棋子记录到反斜棋型当中
			}

			// 正斜杠方向棋型
			if (x - i >= 0 && y - i >= 0) {// 如果没有走出边界
				model[3][5 - i] = chessmanArray[x - i][y - i];// 将左上方棋子记录到正斜棋型当中
			}
			if (x + i <= 14 && y + i <= 14) {// 如果没有走出边界
				model[3][5 + i] = chessmanArray[x + i][y + i];// 将右下方棋子记录到正斜棋型当中
			}
		}
		int score = 0;// 记录最大评分（威胁值）
		int direction = -1;// 记录最大评分的方向（model数组一维下标）
		int index = 0;// 记录坐标偏移量(judgeModle()方法给予的破解位置)
		for (int i = 0; i < model.length; i++) {// 遍历棋型数组
			int getResult[] = judgeModle(model[i]);// 针对此方向棋型，给予破解方案
			if (score < getResult[1]) {// 如果出现比当前最大威胁值还要大的威胁
				score = getResult[1];// 更新最大分（威胁值）
				// 被捕捉的棋子在模型中的索引为5，getResult[0]为破解方案中的下棋索引位置
				// getResult[0] - 5 = 破解位置距离被捕捉的棋子的索引位置
				index = getResult[0] - 5;//
				direction = i;// 记录此棋型的方向
			}
		}
		switch (direction) {// 判断最大威胁值所在的方向
			case 0:// 如果是水平方向
				x += index;// 下棋的位置是原位置向右（或向左）偏移index的值
				break;
			case 1:// 如果是垂直方向
				y += index;// 下棋的位置是原位置向下（或向上）偏移index的值
				break;
			case 2:// 如果是反斜方向
				x += index;// 下棋的位置是原位置向右（或向左）偏移index的值
				y -= index;// 下棋的位置是原位置向上（或向下）偏移index的值
				break;
			case 3:// 如果是正斜方向
				x += index;// 下棋的位置是原位置向右（或向左）偏移index的值
				y += index;// 下棋的位置是原位置向下（或向上）偏移index的值
				break;
		}
		position[0] = score;// 记录此棋子的最大评分（威胁值）
		position[1] = x;// 记录对应下棋横坐标
		position[2] = y;// 记录对应下棋纵坐标

		return position;// 返回结果数组
	}

	/**
	 * 判断某一行棋型的威胁值和相应下棋位置。
	 * 首先将这一行数组转换为字符串，然后查找字符串中是否出现了棋型库中的有威胁的棋型。对比所有威胁棋型，找出其中威胁值最大的
	 * ，记录此棋型的威胁值和落棋点，返回成数组。
	 *
	 * @param model
	 *            -一行棋型数组
	 * @return
	 */
	public int[] judgeModle(int model[]) {
		int piont[] = new int[2];// 初始化返回结果数组
		int score = 0;// 记录最大评分
		StringBuffer sb = new StringBuffer();// 准备将棋型数组保存为字符串的StringBuffer
		for (int num : model) {// 遍历数组，将数组变成字符串
			if (num == GobangModel.BLACK_CHESSMAN) {// 如果是黑子
				num = 4;// 改为其他数字，以免负号会占字符
			}
			sb.append(num);// 字符串添加此数字
		}
		Object library[][] = getModelLibrary();// 获取棋型库中所有棋型及其解决方案
		for (int i = 0; i < library.length; i++) {// 遍历棋型库
			String chessModel = (String) library[i][0];// 获取库中棋型
			int modelIndex = -1;// 临时变量，用于保存某棋型在字符串中出现的索引位置
			if ((modelIndex = sb.indexOf(chessModel)) != -1) {// 如果存在此棋型，则将棋型出现的位置付给modelIndex
				int scoreInLib = (int) library[i][1];// 获取棋型评分
				int stepIndex = (int) library[i][2];// 获取对应的下棋位置
				if (score < scoreInLib) {// 如果出现更高的评分
					score = scoreInLib;// 更新最大分
					// 记录应该(在这一行中)下棋的实际索引位置。
					// 棋型在字符串中的索引位置 + 棋型给出的解决位置 = 字符串中的解决位置
					piont[0] = modelIndex + stepIndex;
					piont[1] = score;// 记录最大分数
				}
			}
		}
		return piont;// 返回结果数组
	}

	/**
	 * 获取棋型库。 棋型库每一种棋型都包含三个数据：第一个数据是棋型，1表示棋子，0表示空位置，例如"11101"；第二个数据是此棋型的评分，
	 * 计算机根据评分来判断要优先破解哪种棋型
	 * ；第三个值是破解该棋型的位置，此位置是此棋型字符串的索引位置，例如"11101"的破解位置为3，就是在0的位置下子
	 * 。所有的棋型都指一行中出现的棋型，只能判断较为简单的棋型，不能判断复杂的多行复合棋型。
	 *
	 * @return 返回棋型匹配库，根据棋型估值降序排列
	 */
	private Object[][] getModelLibrary() {
		// 棋型库数组，第一列保存棋型字符串，第二列保存棋型估值,第三列保存针对此棋型的下法
		Object[][] library = new Object[28][3];
		// 一行可能出现的棋型，1表示棋子，0表示空位置
		String livefour = "011110";// 活四棋型
		String deadfour1a = "01111";// 死四棋型1
		String deadfour1b = "11110";
		String deadfour2a = "11101";// 死四棋型2
		String deadfour2b = "10111";
		String deadfour3 = "11011";// 死四棋型3
		String livethree = "01110";// 活三棋型
		String deadthree1a = "11100";// 死三棋型1
		String deadthree1b = "00111";
		String deadthree2a = "01011";// 死三棋型2
		String deadthree2b = "10110";
		String deadthree2c = "01101";
		String deadthree2d = "11010";
		String deadthree3a = "10011";// 死三棋型3
		String deadthree3b = "11001";
		String deadthree4 = "10101";// 死三棋型4
		String livetwo = "00011000";// 活二棋型
		String deadtwo1a = "11000";// 死二棋型1
		String deadtwo1b = "01100";
		String deadtwo1c = "00110";
		String deadtwo1d = "00011";
		String deadtwo2a = "00101";// 死二棋型2
		String deadtwo2b = "10100";
		String deadtwo2c = "01010";
		String deadtwo3a = "01001";// 死二棋型3
		String deadtwo3b = "10010";
		String deadone1 = "00001";// 死一棋型
		String deadone2 = "10000";
		library[0][0] = livefour;// 棋型放入数组中
		library[0][1] = 100000;// 此棋型产生的威胁值（或优势值）
		library[0][2] = 0;// 在此棋型0索引出下子可破解
		library[1][0] = deadfour1a;
		library[1][1] = 2500;
		library[1][2] = 0;
		library[2][0] = deadfour1b;
		library[2][1] = 2500;
		library[2][2] = 4;
		library[3][0] = deadfour2a;
		library[3][1] = 3300;
		library[3][2] = 3;
		library[4][0] = deadfour2b;
		library[4][1] = 3300;
		library[4][2] = 1;
		library[5][0] = deadfour3;
		library[5][1] = 2600;
		library[5][2] = 2;
		library[6][0] = livethree;
		library[6][1] = 3000;
		library[6][2] = 0;
		library[7][0] = deadthree1a;
		library[7][1] = 500;
		library[7][2] = 3;
		library[8][0] = deadthree1b;
		library[8][1] = 500;
		library[8][2] = 1;
		library[9][0] = deadthree2a;
		library[9][1] = 800;
		library[9][2] = 2;
		library[10][0] = deadthree2b;
		library[10][1] = 800;
		library[10][2] = 1;
		library[11][0] = deadthree2c;
		library[11][1] = 800;
		library[11][2] = 3;
		library[12][0] = deadthree2d;
		library[12][1] = 800;
		library[12][2] = 2;
		library[13][0] = deadthree3a;
		library[13][1] = 600;
		library[13][2] = 2;
		library[14][0] = deadthree3b;
		library[14][1] = 600;
		library[14][2] = 2;
		library[15][0] = deadthree4;
		library[15][1] = 550;
		library[15][2] = 1;
		library[16][0] = livetwo;
		library[16][1] = 650;
		library[16][2] = 2;
		library[17][0] = deadtwo1a;
		library[17][1] = 150;
		library[17][2] = 2;
		library[18][0] = deadtwo1b;
		library[18][1] = 150;
		library[18][2] = 3;
		library[19][0] = deadtwo1c;
		library[19][1] = 150;
		library[19][2] = 1;
		library[20][0] = deadtwo1d;
		library[20][1] = 150;
		library[20][2] = 2;
		library[21][0] = deadtwo2a;
		library[21][1] = 250;
		library[21][2] = 1;
		library[22][0] = deadtwo2b;
		library[22][1] = 250;
		library[22][2] = 3;
		library[23][0] = deadtwo2c;
		library[23][1] = 250;
		library[23][2] = 2;
		library[24][0] = deadtwo3a;
		library[24][1] = 200;
		library[24][2] = 2;
		library[25][0] = deadtwo3b;
		library[25][1] = 200;
		library[25][2] = 2;
		library[26][0] = deadone1;
		library[26][1] = 100;
		library[26][2] = 3;
		library[27][0] = deadone2;
		library[27][1] = 100;
		library[27][2] = 1;

		/*----- 将棋型库数组按照棋型估值（威胁值或优势值）升序排列，确保最后判断最有威胁的棋型---- */
		int index;// 快速排序索引标记
		for (int i = 1; i < library.length; i++) {// 遍历数组行
			index = 0;// 索引标记归0
			for (int j = 1; j <= library.length - i; j++) {// 遍历数组列
				int valueA = (int) library[j][1];// 记录当前行的估值
				int valueB = (int) library[index][1];// 记录索引行的估值
				if (valueA > valueB) {// 如果当前行比索引行评分高
					index = j;// 将当前航记录为索引行
				}
			}
			// 将评分最大的行放到数组的最末尾
			// 交换估值
			Object value = library[library.length - i][1]; // 把第一个元素值保存到临时变量中
			library[library.length - i][1] = library[index][1]; // 把第二个元素值保存到第一个元素单元中
			library[index][1] = value; // 把临时变量也就是第一个元素原值保存到第二个元素中
			// 交换棋型
			Object key = library[library.length - i][0];
			library[library.length - i][0] = library[index][0];
			library[index][0] = key;
			// 交换破解位置
			Object step = library[library.length - i][2];
			library[library.length - i][2] = library[index][2];
			library[index][2] = step;
		}
		/*------------------------快速排序完毕-----------------*/
		return library;
	}
}