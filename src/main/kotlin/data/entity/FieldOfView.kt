package data.entity

import data.Cell

data class FieldOfView(
    var sl: Cell? = null,
    var f1: Cell? = null,
    var fr: Cell? = null,
    var r: Cell? = null,
    var br: Cell? = null,
    var b: Cell? = null,
    var bl: Cell? = null,
    var l: Cell? = null,
    var fl: Cell? = null,
    var f2: Cell? = null,
    var f1r2: Cell? = null,
    var f1l2: Cell? = null,
    var fr2: Cell? = null,
    var fl2: Cell? = null,
    var f3: Cell? = null,
    var f2r3: Cell? = null,
    var f2l3: Cell? = null,
    var f1r3: Cell? = null,
    var f1l3: Cell? = null,
    var fr3: Cell? = null,
    var fl3: Cell? = null,
    var f4: Cell? = null,
    var f3r4: Cell? = null,
    var f3l4: Cell? = null,
    var f2r4: Cell? = null,
    var f2l4: Cell? = null,
    var f1r4: Cell? = null,
    var f1l4: Cell? = null,
    var fr4: Cell? = null,
    var fl4: Cell? = null
)

fun FieldOfView.getFront() = listOf(f1, f2, f3, f4)
fun FieldOfView.getImmediateFront() = f1
fun FieldOfView.getRight() = listOf(br, r, fr, f1r2, fr2, f2r3, f1r3, fr3, f3r4, f2r4, f1r4, fr4)
fun FieldOfView.getImmediateRight() = r
fun FieldOfView.getBehind() = listOf(bl, b, br)
fun FieldOfView.getImmediateBehind() = b
fun FieldOfView.getLeft() = listOf(bl, l, fl, f1l2, fl2, f2l3, f1l3, fl3, f3l4, f2l4, f1l4, fl4)
fun FieldOfView.getImmediateLeft() = l
fun FieldOfView.getSameLocation() = l
fun FieldOfView.getAhead() = listOf(
    fl, f1, fr, fl2, f1l2, f2, f1r2, fr2, fl3, f1l3, f2l3, f3, f2r3, f1r3, fr3, fl4, f1l4, f2l4, f3l4, f4, f3r4,
    f2r4, f1r4, fr4
)

fun FieldOfView.getNeighborhood() = listOf(fl, f1, fr, r, br, b, bl, l)