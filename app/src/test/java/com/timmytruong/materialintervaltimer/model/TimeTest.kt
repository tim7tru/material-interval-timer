package com.timmytruong.materialintervaltimer.model

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec

private const val EMPTY_INPUT = "000000"
private const val EMPTY_TIME = "00"
private const val ZERO = "0"
private const val ZERO_L = 0L

class TimeTest : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    Given("initializing time") {
        When("initial total is empty") {
            val time = Time()
            Then("input format is empty") { assert(time.getFormat() == EMPTY_INPUT) }
            Then("total time is 0") { assert(time.getTotalTime() == ZERO_L) }
            Then("input is invalid") { assert(!time.isInputValid()) }

            And("remove is called") {
                time.removeFromInput()
                Then("input is empty") { assert(time.getFormat() == EMPTY_INPUT) }
                Then("time is invalid") { assert(!time.isInputValid()) }
            }

            And("one is added to input") {
                ONE_SEC_NORM.forEach { time.addToInput(it.toString()) }
                Then("format is one sec norm") { assert(time.getFormat() == ONE_SEC_NORM) }

                And("finalize is called") {
                    time.finalize()
                    Then("total time is calculated") { assert(time.getTotalTime() == ONE_SEC) }
                }

                And("one is removed from input") {
                    time.removeFromInput()
                    Then("input is empty") { assert(time.getFormat() == EMPTY_INPUT) }
                    Then("time is invalid") { assert(!time.isInputValid()) }
                }
            }

            And("time is filled") {
                FULL_TIME.forEach { time.addToInput(it.toString()) }
                Then("input is full") { assert(time.getFormat() == FULL_TIME) }
                Then("time is valid") { assert(time.isInputValid()) }

                And("add to input is called") {
                    time.addToInput(ZERO)
                    Then("input is still full") { assert(time.getFormat() == FULL_TIME) }
                    Then("time is still valid") { assert(time.isInputValid()) }
                }
            }

            And("hours is called") {
                And("needed from input") {
                    Then("hours is empty") { assert(time.hours(true) == EMPTY_TIME) }
                }
                And("needed from total") {
                    Then("hours is empty") { assert(time.hours(false) == EMPTY_TIME) }
                }
            }

            And("minutes is called") {
                And("needed from input") {
                    Then("mins is empty") { assert(time.minutes(true) == EMPTY_TIME) }
                }
                And("needed from total") {
                    Then("mins is empty") { assert(time.minutes(false) == EMPTY_TIME) }
                }
            }

            And("seconds is called") {
                And("needed from input") {
                    Then("secs is not empty") { assert(time.seconds(true) == EMPTY_TIME) }
                }
                And("needed from total") {
                    Then("sec is not empty") { assert(time.seconds(false) == EMPTY_TIME) }
                }
            }
        }
        When("initial time is not empty") {
            val time = Time(ONE_SEC)
            Then("input format is normalized") { assert(time.getFormat() == ONE_SEC_NORM) }
            Then("total time is one second") { assert(time.getTotalTime() == ONE_SEC) }
            Then("input is valid") { assert(time.isInputValid()) }

            And("hours is called") {
                And("needed from input") {
                    Then("hours is empty") { assert(time.hours(true) == ONE_SEC_HOUR) }
                }
                And("needed from total") {
                    Then("hours is empty") { assert(time.hours(false) == ONE_SEC_HOUR) }
                }
            }

            And("minutes is called") {
                And("needed from input") {
                    Then("mins is empty") { assert(time.minutes(true) == ONE_SEC_MIN) }
                }
                And("needed from total") {
                    Then("mins is empty") { assert(time.minutes(false) == ONE_SEC_MIN) }
                }
            }

            And("seconds is called") {
                And("needed from input") {
                    Then("secs is not empty") { assert(time.seconds(true) == ONE_SEC_SEC) }
                }
                And("needed from total") {
                    Then("sec is not empty") { assert(time.seconds(false) == ONE_SEC_SEC) }
                }
            }
        }
    }
})