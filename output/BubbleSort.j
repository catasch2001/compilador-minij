.class public BubbleSort
.super java/lang/Object

.method public <init>()V
	.limit stack 1
	.limit locals 1
	aload_0
	invokespecial java/lang/Object/<init>()V
	return
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 20
	.limit locals 1
	getstatic java/lang/System/out Ljava/io/PrintStream;
	new BBS
	dup
	invokespecial BBS/<init>()V
	ldc 10
	invokevirtual BBS/Start(I)I
	invokevirtual java/io/PrintStream/println(I)V
	return
.end method
