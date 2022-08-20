package com.example.kotlincalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private var expression = ""
    private var valueHasPointAlready = false
    private var lastCharIsDigit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val expressionTV = findViewById<TextView>(R.id.expressionTV)
        val resultTV = findViewById<TextView>(R.id.resultTV)

        // Add Event Listeners to Numeric Buttons
        val numericButtonIds = arrayOf(
            R.id.btnZero,
            R.id.btnOne,
            R.id.btnTwo,
            R.id.btnThree,
            R.id.btnFour,
            R.id.btnFive,
            R.id.btnSix,
            R.id.btnSeven,
            R.id.btnEight,
            R.id.btnNine
        )

        for (id in numericButtonIds) {
            findViewById<Button>(id).setOnClickListener{
                onClickNumericButton(it, expressionTV, resultTV)
            }
        }

        // Point button
        findViewById<Button>(R.id.btnPoint).setOnClickListener{
            onClickPointButton(it, expressionTV)
        }

        // Operators buttons
        val operatorButtonIds = arrayOf(
            R.id.btnAdd,
            R.id.btnSubtract,
            R.id.btnMultiply,
            R.id.btnDivide
        )

        for (id in operatorButtonIds) {
            findViewById<Button>(id).setOnClickListener{
                onClickOperatorButton(it, expressionTV, resultTV)
            }
        }

        // Equal button
        findViewById<Button>(R.id.btnEqual).setOnClickListener{
            onClickEqualButton(expressionTV, resultTV)
        }

        // C Button
        findViewById<Button>(R.id.btnC).setOnClickListener{
            expression = ""
            expressionTV.text = ""
            resultTV.text = ""

            lastCharIsDigit = false
            valueHasPointAlready = false
        }

        // Backspace Button
        findViewById<Button>(R.id.btnBackspace).setOnClickListener{
            if (expression.isNotEmpty()) {
                val lastChar = expression[expression.length - 1]

                if (lastChar == '.') {
                    valueHasPointAlready = false
                } else if (containsOperator(lastChar.toString())) {
                    lastCharIsDigit = true
                }

                expression = expression.substring(0, expression.length - 1)
                expressionTV.text = expression
            }
        }
    }

    private fun onClickPointButton(view: View, expressionTV: TextView) {
        if (!valueHasPointAlready) {
            expression += (view as Button).text
            expressionTV.text = expression

            valueHasPointAlready = true
        }
    }

    private fun onClickNumericButton(view: View, expressionTV: TextView, resultTV: TextView) {
        lastCharIsDigit = true

        if (resultTV.text.isNotEmpty() && expression.last().isDigit()) {
            resultTV.text = ""
            expression = ""
            expressionTV.text = ""
        }

        expression += (view as Button).text
        expressionTV.text = expression
    }

    private fun onClickOperatorButton(view: View, expressionTV: TextView, resultTV: TextView) {
        if (containsOperator(expression) && lastCharIsDigit) {
            calculateAndDisplayResult(expressionTV, resultTV)
        }

        val operator = (view as Button).text

        val addingSubtractInBegging = operator == "-" && expression.isEmpty()
        if (lastCharIsDigit || addingSubtractInBegging) {
            expression += operator
            expressionTV.text = expression

            lastCharIsDigit = false
            valueHasPointAlready = false
        }
    }

    private fun onClickEqualButton(expressionTV: TextView, resultTV: TextView) {
        calculateAndDisplayResult(expressionTV, resultTV)
    }

    private fun calculateAndDisplayResult(expressionTV: TextView, resultTV: TextView) {
        val startsWithSubtract = expression.startsWith("-")

        val regexOperators = Regex("""[+|\-×÷]""")
        val terms = if (!startsWithSubtract)
                expression.split(regexOperators).filter{ term -> term.isNotEmpty() }
            else
                expression.substring(1).split(regexOperators).filter{ term -> term.isNotEmpty() }

        if (terms.size != 2) {
            Toast.makeText(this, "Formato inválido!", Toast.LENGTH_LONG).show()
            return
        }

        try {
            val firstNumberString = if (startsWithSubtract) "-${terms[0]}" else terms[0]
            val secondNumberString = terms[1]
            val operator = expression[firstNumberString.length]
            var result: Double;

            val firstNumberDouble = firstNumberString.toDouble()
            val secondNumberDouble = secondNumberString.toDouble()

            when (operator) {
                '+' -> {
                    result = firstNumberDouble + secondNumberDouble
                }
                '-' -> {
                    result = firstNumberDouble - secondNumberDouble
                }
                '×' -> {
                    result = firstNumberDouble * secondNumberDouble
                }
                '÷' -> {
                    result = firstNumberDouble / secondNumberDouble
                }
                else -> {
                    return
                }
            }

            expression = result.toString()

            if (expression.endsWith(".0")) {
                expression = expression.substring(0, expression.length - 2)
            }

            resultTV.text = expression
            expressionTV.text = expression
        } catch (err: Error) {
            Toast.makeText(this, "Erro!", Toast.LENGTH_LONG).show()
        }
    }

    private fun containsOperator(expression: String): Boolean {
        val startsWithSubtract = expression.startsWith("-")
        val regexOperators = Regex("""[+|\-×÷]""")

        return if(!startsWithSubtract)
                expression.contains(regexOperators)
            else
                expression.substring(1).contains(regexOperators)
    }
}