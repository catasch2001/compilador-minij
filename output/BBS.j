.class public BBS
.super java/lang/Object

.field private number [I
.field private size I

.method public <init>()V
	.limit stack 1
	.limit locals 1
	aload_0
	invokespecial java/lang/Object/<init>()V
	return
.end method

.method public Start(I)I
	.limit stack 20
	.limit locals 3
	aload_0
	iload 1
	invokevirtual BBS/Init(I)I
	istore 2
	aload_0
	invokevirtual BBS/Print()I
	istore 2
	getstatic java/lang/System/out Ljava/io/PrintStream;
	ldc 99999
	invokevirtual java/io/PrintStream/println(I)V
	aload_0
	invokevirtual BBS/Sort()I
	istore 2
	aload_0
	invokevirtual BBS/Print()I
	istore 2
	ldc 0
    ireturn
.end method

.method public Sort()I
	.limit stack 20
	.limit locals 10
	aload_0
	getfield BBS/size I
	ldc 1
    isub
	istore 2
	ldc 0
	ldc 1
    isub
	istore 3
L0:
	iload 3
	iload 2
	if_icmplt L2
	iconst_0
	goto L3
L2:
	iconst_1
L3:
	ifeq L1
	ldc 1
	istore 8
L4:
	iload 8
	iload 2
	ldc 1
    iadd
	if_icmplt L6
	iconst_0
	goto L7
L6:
	iconst_1
L7:
	ifeq L5
	iload 8
	ldc 1
    isub
	istore 7
	aload_0
	getfield BBS/number [I
	iload 7
    iaload
	istore 4
	aload_0
	getfield BBS/number [I
	iload 8
    iaload
	istore 5
	iload 5
	iload 4
	if_icmplt L10
	iconst_0
	goto L11
L10:
	iconst_1
L11:
	ifeq L8
	iload 8
	ldc 1
    isub
	istore 6
	aload_0
	getfield BBS/number [I
	iload 6
    iaload
	istore 9
    aload_0
    getfield BBS/number [I
	iload 6
	aload_0
	getfield BBS/number [I
	iload 8
    iaload
    iastore
    aload_0
    getfield BBS/number [I
	iload 8
	iload 9
    iastore
	goto L9
L8:
	ldc 0
	istore 1
L9:
	iload 8
	ldc 1
    iadd
	istore 8
	goto L4
L5:
	iload 2
	ldc 1
    isub
	istore 2
	goto L0
L1:
	ldc 0
    ireturn
.end method

.method public Print()I
	.limit stack 20
	.limit locals 2
	ldc 0
	istore 1
L12:
	iload 1
	aload_0
	getfield BBS/size I
	if_icmplt L14
	iconst_0
	goto L15
L14:
	iconst_1
L15:
	ifeq L13
	getstatic java/lang/System/out Ljava/io/PrintStream;
	aload_0
	getfield BBS/number [I
	iload 1
    iaload
	invokevirtual java/io/PrintStream/println(I)V
	iload 1
	ldc 1
    iadd
	istore 1
	goto L12
L13:
	ldc 0
    ireturn
.end method

.method public Init(I)I
	.limit stack 20
	.limit locals 2
	aload_0
	iload 1
	putfield BBS/size I
	aload_0
	iload 1
    newarray int
	putfield BBS/number [I
    aload_0
    getfield BBS/number [I
	ldc 0
	ldc 20
    iastore
    aload_0
    getfield BBS/number [I
	ldc 1
	ldc 7
    iastore
    aload_0
    getfield BBS/number [I
	ldc 2
	ldc 12
    iastore
    aload_0
    getfield BBS/number [I
	ldc 3
	ldc 18
    iastore
    aload_0
    getfield BBS/number [I
	ldc 4
	ldc 2
    iastore
    aload_0
    getfield BBS/number [I
	ldc 5
	ldc 11
    iastore
    aload_0
    getfield BBS/number [I
	ldc 6
	ldc 6
    iastore
    aload_0
    getfield BBS/number [I
	ldc 7
	ldc 9
    iastore
    aload_0
    getfield BBS/number [I
	ldc 8
	ldc 19
    iastore
    aload_0
    getfield BBS/number [I
	ldc 9
	ldc 5
    iastore
	ldc 0
    ireturn
.end method

