.class public Fac
.super java/lang/Object


.method public <init>()V
	.limit stack 1
	.limit locals 1
	aload_0
	invokespecial java/lang/Object/<init>()V
	return
.end method

.method public ComputeFac(I)I
	.limit stack 20
	.limit locals 3
	iload 1
	ldc 1
	if_icmplt L2
	iconst_0
	goto L3
L2:
	iconst_1
L3:
	ifeq L0
	ldc 1
	istore 2
	goto L1
L0:
	iload 1
	aload_0
	iload 1
	ldc 1
    isub
	invokevirtual Fac/ComputeFac(I)I
    imul
	istore 2
L1:
	iload 2
    ireturn
.end method

