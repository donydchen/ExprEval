package parser;

public class OPP {

		/**
		 * 缺少左括号，-7
		 */
		public static final int ERRLEFTPAR = -7;
		/**
		 * 语法错误， -6
		 */
		public static final int ERRSYN = -6;
		/**
		 * 缺少操作数， -5
		 */
		public static final int ERROPERAND = -5;
		/**
		 * 类型错误， -4
		 */
		public static final int ERRTYPE = -4;
		/**
		 * 函数语法错误， -3
		 */
		public static final int ERRFUNCSYN = -3;
		/**
		 * 缺少右括号， -2
		 */
		public static final int ERRRIGHTPAR = -2;
		/**
		 * 三元运算异常， -1
		 */
		public static final int ERRTRINA = -1;
		/**
		 * 接受, 0
		 */
		public static final int ACCEPT = 0; 
		/**
		 * 单目运算， 1。包括-和！
		 */
		public static final int RDUNAOPER = 1;
		/**
		 * 双目运算归约， 2。包括+, -, *, /, ^, &, |以及关系运算。
		 */
		public static final int RDBINAOPER = 2;
		/**
		 * 三目运算归约， 3。包括?:
		 */
		public static final int RDTRINAOPER = 3; 
		/**
		 * 括号运算归约， 4。包含函数检测及运算，
		 */
		public static final int RDMATCH = 4;
		/**
		 * 移入操作， 5。
		 */
		public static final int SHIFT = 5;
		
	/**
	 * 移入归约表。其中行代表的是栈顶符号，列代表的是读入符号，而数字和上述静态变量相对应。
	 * func指的是函数，包括cos, sin, max, min
	 * md指的是multiple和divide，即乘除运算
	 * pm指的是plus和minus，即加减运算
	 * -指的是负号
	 * cmp指的关系运算，即大于,小于等等
	 * 另外，由于（）运算优先级最高，所以遇到）便进行归约，不压栈，所以栈顶符号中的）仅仅只是为了后续操作方便，没有实质意义。
	 */
	public static final int table[][] = {
		/*栈顶*/ /*(  ) func - ^  md pm cmp ! &  |  ?  :  ,  $    读入字符*/
		/*(*/    {5, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,-1, 5,-2},
		/*)*/    {4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4},
		/*func*/ {5,-3,-3,-3,-3,-3,-3,-3,-3,-3,-3,-3,-3,-3,-3},
		/*-*/    {5, 4, 5, 5, 1, 1, 1, 1,-6,-4,-4, 1, 1, 1, 1},
		/*^*/    {5, 4, 5, 5, 5, 2, 2, 2,-6,-4,-4, 2, 2, 2, 2},
		/*md*/   {5, 4, 5, 5, 5, 2, 2, 2,-6,-4,-4, 2, 2, 2, 2},
		/*pm*/   {5, 4, 5, 5, 5, 5, 2, 2,-6,-4,-4, 2, 2, 2, 2},
		/*cmp*/  {5, 4, 5, 5, 5, 5, 5,-4,-6, 2, 2, 2,-1,-3, 2},
		/*!*/    {5, 4,-4,-4,-4,-4,-4, 5, 5, 1, 1, 1,-1,-3, 1},
		/*&*/    {5, 4,-4,-4,-4,-4,-4, 5, 5, 2, 2, 2,-1,-3, 2},
		/*|*/    {5, 4,-4,-4,-4,-4,-4, 5, 5, 5, 2, 2,-1,-3, 2},
		/*?*/    {5,-1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,-1,-1},
		/*:*/    {5, 4, 5, 5, 5, 5, 5,-1,-1,-1,-1, 5,-1,-1, 3},
		/*,*/    {5, 4, 5, 5, 5, 5, 5,-3,-3,-3,-3, 5,-1, 5,-3},
		/*$*/    {5,-7, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,-1,-3, 0}
	};
}
