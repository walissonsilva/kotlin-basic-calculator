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
                onClickNumericButton(it, expressionTV)
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

    private fun onClickNumericButton(view: View, expressionTV: TextView) {
        lastCharIsDigit = true

        expression += (view as Button).text
        expressionTV.text = expression
    }

    private fun onClickOperatorButton(view: View, expressionTV: TextView, resultTV: TextView) {
        if (containsOperator(expression) && lastCharIsDigit) {
            calculateAndDisplayResult(expressionTV, resultTV)
        }

        val operator = (view as Button).text

        if (lastCharIsDigit || operator == "-") {
            expression += operator
            expressionTV.text = expression

            lastCharIsDigit = false
            valueHasPointAlready = false
        }
    }

    private fun onClickEqualButton(expressionTV: TextView, resultTV: TextView) {
        if (containsOperator(expression) && lastCharIsDigit) {
            calculateAndDisplayResult(expressionTV, resultTV)
        }
    }

    private fun calculateAndDisplayResult(expressionTV: TextView, resultTV: TextView) {
        val regexOperators = Regex("""[+|\-|×|÷]""")
        val terms = expression.split(regexOperators)

        if (terms.size != 2) {
            Toast.makeText(this, "Formato inválido!", Toast.LENGTH_LONG).show()
            return
        }

        val firstTerm = terms[0]
        val secondTerm = terms[1]
        val operator = expression[firstTerm.length]
        var result: Double

        when (operator) {
            '+' -> {
                result = firstTerm.toDouble() + secondTerm.toDouble()
            }
            '-' -> {
                result = firstTerm.toDouble() - secondTerm.toDouble()
            }
            '×' -> {
                result = firstTerm.toDouble() * secondTerm.toDouble()
            }
            '÷' -> {
                result = firstTerm.toDouble() / secondTerm.toDouble()
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
    }

    private fun containsOperator(expression: String): Boolean {
        val regexOperators = Regex("""[+|\-×÷]""")
        return expression.contains(regexOperators)
    }
}