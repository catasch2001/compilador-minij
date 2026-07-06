.class public QS
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
	invokevirtual QS/Init(I)I
	istore 2
	aload_0
	invokevirtual QS/Print()I
	istore 2
	getstatic java/lang/System/out Ljava/io/PrintStream;
	ldc 9999
	invokevirtual java/io/PrintStream/println(I)V
	aload_0
	getfield QS/size I
	ldc 1
    isub
	istore 2
	aload_0
	iload 2
	ldc 0
	invokevirtual QS/Sort(II)I
	istore 2
	aload_0
	invokevirtual QS/Print()I
	istore 2
	ldc 0
    ireturn
.end method

.method public Sort(II)I
	.limit stack 20
	.limit locals 11
	ldc 0
	istore 7
	iload 2
	iload 1
	if_icmplt L2
	iconst_0
	goto L3
L2:
	iconst_1
L3:
	ifeq L0
	aload_0
	getfield QS/number [I
	iload 1
	iaload
	istore 3
	iload 2
	ldc 1
    isub
	istore 4
	iload 1
	istore 5
	ldc 1
	istore 8
L4:
	ldc 1
	iload 8
	if_icmpeq L6
	iconst_0
	goto L7
L6:
	iconst_1
L7:
	ifeq L5
	ldc 1
	istore 9
L8:
	ldc 1
	iload 9
	if_icmpeq L10
	iconst_0
	goto L11
L10:
	iconst_1
L11:
	ifeq L9
	iload 4
	ldc 1
    iadd
	istore 4
	aload_0
	getfield QS/number [I
	iload 4
	iaload
	istore 10
	iload 10
	iload 3
	if_icmpgt L16
	iconst_0
	goto L17
L16:
	iconst_1
L17:
	ifne L14
	iload 10
	iload 3
	if_icmpeq L18
	iconst_0
	goto L19
L18:
	iconst_1
L19:
	ifne L14
	iconst_0
	goto L15
L14:
	iconst_1
L15:
	ifeq L12
	ldc 0
	istore 9
	goto L13
L12:
	ldc 1
	istore 9
L13:
	goto L8
L9:
	ldc 1
	istore 9
L20:
	ldc 1
	iload 9
	if_icmpeq L22
	iconst_0
	goto L23
L22:
	iconst_1
L23:
	ifeq L21
	iload 5
	ldc 1
    isub
	istore 5
	aload_0
	getfield QS/number [I
	iload 5
	iaload
	istore 10
	iload 3
	iload 10
	if_icmpgt L28
	iconst_0
	goto L29
L28:
	iconst_1
L29:
	ifne L26
	iload 3
	iload 10
	if_icmpeq L30
	iconst_0
	goto L31
L30:
	iconst_1
L31:
	ifne L26
	iconst_0
	goto L27
L26:
	iconst_1
L27:
	ifeq L24
	ldc 0
	istore 9
	goto L25
L24:
	ldc 1
	istore 9
L25:
	goto L20
L21:
	aload_0
	getfield QS/number [I
	iload 4
	iaload
	istore 7
	aload_0
	getfield QS/number [I
	iload 4
	aload_0
	getfield QS/number [I
	iload 5
	iaload
	iastore
	aload_0
	getfield QS/number [I
	iload 5
	iload 7
	iastore
	iload 5
	iload 4
	ldc 1
    iadd
	if_icmplt L34
	iconst_0
	goto L35
L34:
	iconst_1
L35:
	ifeq L32
	ldc 0
	istore 8
	goto L33
L32:
	ldc 1
	istore 8
L33:
	goto L4
L5:
	aload_0
	getfield QS/number [I
	iload 5
	aload_0
	getfield QS/number [I
	iload 4
	iaload
	iastore
	aload_0
	getfield QS/number [I
	iload 4
	aload_0
	getfield QS/number [I
	iload 1
	iaload
	iastore
	aload_0
	getfield QS/number [I
	iload 1
	iload 7
	iastore
	aload_0
	iload 4
	ldc 1
    isub
	iload 2
	invokevirtual QS/Sort(II)I
	istore 6
	aload_0
	iload 1
	iload 4
	ldc 1
    iadd
	invokevirtual QS/Sort(II)I
	istore 6
	goto L1
L0:
	ldc 0
	istore 6
L1:
	ldc 0
    ireturn
.end method

.method public Print()I
	.limit stack 20
	.limit locals 2
	ldc 0
	istore 1
L36:
	iload 1
	aload_0
	getfield QS/size I
	if_icmplt L38
	iconst_0
	goto L39
L38:
	iconst_1
L39:
	ifeq L37
	getstatic java/lang/System/out Ljava/io/PrintStream;
	aload_0
	getfield QS/number [I
	iload 1
	iaload
	invokevirtual java/io/PrintStream/println(I)V
	iload 1
	ldc 1
    iadd
	istore 1
	goto L36
L37:
	ldc 0
    ireturn
.end method

.method public Init(I)I
	.limit stack 20
	.limit locals 2
	aload_0
	iload 1
	putfield QS/size I
	aload_0
	iload 1
	newarray int
	putfield QS/number [I
	aload_0
	getfield QS/number [I
	ldc 0
	ldc 20
	iastore
	aload_0
	getfield QS/number [I
	ldc 1
	ldc 7
	iastore
	aload_0
	getfield QS/number [I
	ldc 2
	ldc 12
	iastore
	aload_0
	getfield QS/number [I
	ldc 3
	ldc 18
	iastore
	aload_0
	getfield QS/number [I
	ldc 4
	ldc 2
	iastore
	aload_0
	getfield QS/number [I
	ldc 5
	ldc 11
	iastore
	aload_0
	getfield QS/number [I
	ldc 6
	ldc 6
	iastore
	aload_0
	getfield QS/number [I
	ldc 7
	ldc 9
	iastore
	aload_0
	getfield QS/number [I
	ldc 8
	ldc 19
	iastore
	aload_0
	getfield QS/number [I
	ldc 9
	ldc 5
	iastore
	ldc 0
    ireturn
.end method

