import java.io.File
import kotlin.math.abs

var a = -10.0
var b = 15.0/4.0
var c = 10.0
var d = -4.0
var e =  -8.0

fun main(args: Array<String>){
    //Браун-Робинсон
    //brown_robinson(n, matrix, mixedStratA, mixedStratB)
    numbers(5, 0.001)
    analytic()
}
fun check(o: Int, p: Int): Int {
    return if (o - p >= 0) (o - p)
    else 0
}
//----------------------------------------------------------------------------------------------------------------------
fun analytic(){
    val y = (c * d - 2 * a * e) / (4 * a * b - c * c)
    val x = -(c * y + d) / (2 * a)
    val v = calc(x, y)
    println()
    println("Аналитический метод: x = $x, y  = $y, v = $v")
}
//----------------------------------------------------------------------------------------------------------------------
fun numbers(shift: Int, delta: Double) {
    var x = 0.0
    var y = 0.0
    var v = 0.0
    var arrV = mutableListOf<Double>(0.0)
    var iCount = 0
    while ((iCount < shift) || (abs(arrV[check(iCount, shift.toInt())] - v) > delta)) {
        var N = iCount + 2
        println(N)
        var matrix = Array(N) { Array(N) { 0.0 } }
        for (i in 0 until N) {
            for(j in 0 until N) {
                matrix[i][j] = calc((i.toDouble() / N.toDouble()), (j.toDouble() / N.toDouble())) // заполнение таблицы
            }
        }
        printMatrix(matrix)
        //проверка на сделовую точку
        var cMax = col_max(matrix)
        var rMin = row_min(matrix)
        var maxmin = fMax(rMin)
        var minmax = fMin(cMax)
        //println("Минимакс: $minmax и максимин: $maxmin")
        if (minmax == maxmin) {
            x = (rMin.indexOf(maxmin).toDouble()) / (N)
            y = (cMax.indexOf(minmax).toDouble()) / (N)
            v = calc(x, y)
            println("Седловая точка найдена, x = $x, y = $y, v = $v")
        } else {
            var mixedStratB = Array(N){0}
            var mixedStratA = Array(N){0}
            brown_robinson(N, matrix, mixedStratA, mixedStratB)
            var maxA = fMax(mixedStratA)
            var maxB = fMax(mixedStratB)
            x = mixedStratA.indexOf(maxA).toDouble()
            y = mixedStratB.indexOf(maxB).toDouble()
            v = matrix[x.toInt()][y.toInt()]
            println("Седловая точка не найдена, " +
                    "решение методом Брауна-Робертсон x = ${x / (N)}, y = ${y / (N)}, v = ${matrix[x.toInt()][y.toInt()]}")
            x /= N
            y /= N
        }
        arrV.add(v)
        iCount++
        // Конец итераций
    }
    println("------------------------------------------------------------------------------------------------------------")
    println("Конец итераций, x = $x, y = $y, v = $v")
}
//----------------------------------------------------------------------------------------------------------------------
fun calc(x: Double, y: Double): Double {
    return (a*x*x+b*y*y+c*x*y+d*x+e*y)
}

fun fMax(arr: Array<Double>): Double{
    var max: Double = arr[0]
    for (i in arr.indices) {
        if (arr[i] > max) max = arr[i]
    }
    return max
}
fun fMax(arr: Array<Int>): Int{
    var max: Int = arr[0]
    for (i in arr.indices) {
        if (arr[i] > max) max = arr[i]
    }
    return max
}

fun fMin(arr: Array<Double>): Double{
    var min: Double = arr[0]
    for (i in arr.indices) {
        if (arr[i] < min) min = arr[i]
    }
    return min
}

fun col_max(matrix: Array<Array<Double>>): Array<Double> {
    var max = Array<Double>(matrix.size){0.0}
    //println("Максимум")
    for (i in matrix.indices) {

        for (j in matrix[i].indices){
            // print("Элемент матрицы $i-$j: ${matrix[j][i]} > ${max[i]}? ")
            if (j == 0 ) max[i] = matrix[j][i]
            if (matrix[j][i] > max[i]) max[i] = matrix[j][i]
            // println("максимум на этом этапе: ${max[i]}")
        }
        //println("Максимум в столбце $i : ${max[i]}")
    }
    return max
}
fun row_min(matrix: Array<Array<Double>>): Array<Double> {
    //println("Минимум")
    var min = Array<Double>(matrix.size){0.0}
    for (i in matrix.indices) {
        for (j in matrix[i].indices){
            // print("Элемент матрицы $i-$j: ${matrix[i][j]} < ${min[i]}? ")
            if (j == 0) min[j] = matrix[i][j]
            if (matrix[i][j] < min[i]) min[i] = matrix[i][j]
            // println("Минимум на этом этапе: ${min[i]}")
        }
        //println("Минимум в строке $i : ${min[i]}")
    }
    return min
}
//Метод Брауна-Робинсон
fun brown_robinson(n: Int, matrix: Array<Array<Double>>, mixedStratA: Array<Int>, mixedStratB: Array<Int>): Double {
    val outputName = "output.txt"
    var FH: Double  = 0.0
    var FL: Double = 0.0
    val outputStream = File(outputName).bufferedWriter()
    var aGain = Array<Double>(n){0.0}
    var bLoss = Array<Double>(n){0.0}
    var nums = arrayOf<Double>(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    var currA = 0; var currB = 0
    var high = 0.0; var low = 0.0; var e = 0.0
    var iCount = 1
    // Начало итераций
    while (iCount > 0) {
        mixedStratA[currA]++
        mixedStratB[currB]++
        nums[0] = iCount.toDouble()
        nums[1] = currA.toDouble()
        nums[2] = currB.toDouble()
        //----------------------------------
        var nextA = 0; var nextB = 0; var gain = 0.0; var lose = 0.0
        for (i in 0 until n) {
            //Выбор следующей стратегии для первого игрока
            aGain[i] += matrix[i][currB]
            if (aGain[i] > gain || i == 0) {
                gain = aGain[i]
                nextA = i
            } else if (aGain[i] == gain) {
                val rand = (0..1).random()
                if (rand == 1) {
                    gain = aGain[i]
                    nextA = i
                }
            }
            //Выбор следующей стратегии для второго игрока
            bLoss[i] += matrix[currA][i]

            if (bLoss[i] < lose || i == 0) {
                lose = bLoss[i]
                nextB = i;
            } else if (bLoss[i] == lose){
                val rand = (0..1).random()
                if (rand == 1) {
                    lose = bLoss[i]
                    nextB = i
                }
            }
            //КОНЕЦ ЦИКЛА
        }
        //Верхняя и нижняя границы цены игры
        var max = gain/iCount.toDouble()
        var min = lose/iCount.toDouble()
        if (max < high || iCount == 1) {
            high = max
        }
        if (min > low || iCount == 1) {
            low = min
        }
        e = high - low
        FH = high
        FL = low
        nums[9] = max
        nums[10] = min
        nums[11] = e
        //println(String.format("%3d || x %d | y %d || %4d | %4d | %4d || %4d | %4d | %4d || %8f | %8f | %8f ||", nums[0].toInt(), nums[1].toInt() + 1, nums[2].toInt() + 1, nums[3].toInt(), nums[4].toInt(), nums[5].toInt(), nums[6].toInt(), nums[7].toInt(), nums[8].toInt(), nums[9], nums[10], nums[11]))
        //println("-------------------------------------------------------------------------------------------------")
        outputStream.write("${nums[11]}")
        outputStream.newLine()
        //println("$high, $low, $e")
        if (e <= 0.1) {
            break;
        }
        currA = nextA
        currB = nextB
        iCount++
        //КОНЕЦ ИТЕРАЦИЙ
    }

    outputStream.close()
    //println("Нижняя оценка игры - $FL, верхняя оценка игры - $FH, цена игры - ${(FL + FH) / 2}")
    //println(String.format("Смешанная стратегия для первого игрока (%f, %f, %f, %f)", mixedStratA[0].toDouble()/iCount.toDouble(), mixedStratA[1].toDouble()/iCount.toDouble(), mixedStratA[2].toDouble()/iCount.toDouble(), mixedStratA[3].toDouble()/iCount.toDouble()))
    //println(String.format("Смешанная стратегия для второго игрока (%f, %f, %f, %f)", mixedStratB[0].toDouble()/iCount.toDouble(), mixedStratB[1].toDouble()/iCount.toDouble(), mixedStratB[2].toDouble()/iCount.toDouble(), mixedStratB[3].toDouble()/iCount.toDouble()))
    //println("---------------------------------------------------")
    return (FL + FH) / 2
}




fun printMatrix(matrix: Array<Array<Double>>) {
    for (i in matrix.indices) {
        print("[")
        for (j in matrix[i].indices) {
            print(String.format(" [%3f] ", matrix[i][j]))
        }
        println("]")
    }
}



/*

//Умножение строки на матрицу
fun strXmat(str: Array<Float>, mat: SquareMatrix): Array<Float> {
    var m = arrayOf<Float>(0.0f, 0.0f ,0.0f)
    for (i in str.indices) {
        for (n in str.indices) {
            m[i] += mat[n, i]*str[n]
        }
    }
    return m
}
//Умножение матрицы на строку
fun matXstr(mat: SquareMatrix, str: Array<Float>): Array<Float> {
    var m = arrayOf<Float>(0.0f, 0.0f ,0.0f)
    for (i in str.indices) {
        for (n in str.indices) {
            m[i] += mat[i, n]*str[n]
        }
    }
    return m
}
fun stlXstr(str1: Array<Float>, str2: Array<Float>): SquareMatrix {
    return SquareMatrix(str1.size) { row, col ->
        (str1[row] * str2[col])
    }
}
//Умножение строки на столбец
fun strXstl(str1: Array<Float>, str2: Array<Float>): Float {
    var n = 0.0f
    for (i in str1.indices){
        n += str1[i] * str2[i]
    }
    return n
}
//Цена игры аналитическим методом
fun v_res(mat: SquareMatrix): Float {
    var m = arrayOf<Float>(1.0f, 1.0f ,1.0f)
    return (1.0f/(strXstl(strXmat(m, mat.inverse()),m)))
}
//Стратегии первого игрока аналитическим методом
fun x_res(mat: SquareMatrix): Array<Float>{
    var m = arrayOf<Float>(1.0f, 1.0f ,1.0f)
    var v = v_res(mat)
    var x = strXmat(m, mat.inverse())
    for (i in x.indices){
        x[i] *= v
    }
    return x
}
//Стратегии второго игрока аналитическим методом
fun y_res(mat: SquareMatrix): Array<Float>{
    var m = arrayOf<Float>(1.0f, 1.0f ,1.0f)
    var v = v_res(mat)
    var y = matXstr(mat.inverse(), m)
    for (i in y.indices){
        y[i] *= v
    }
    return y
}
//Печать строки
fun printstr(str: Array<Float>) {
    for (i in str.indices){
        print( "${str[i]}")
        if (i != str.size - 1) print(", ")
    }
    println("")
}

fun analytic(mat: SquareMatrix) {
    var v = v_res(mat)
    println(String.format("Цена игры, найденная аналитическим методом %f",v))
    var x = x_res(mat)
    println("Ниже - стратегии первого игрока")
    printstr(x)
    var y = y_res(mat)
    println("Ниже - стратегии второго игрока")
    printstr(y)
}


 */
