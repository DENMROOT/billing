#!/bin/sh

exec java ${JAVA_OPTS} -noverify -XX:+AlwaysPreTouch -cp /app/resources/:/app/classes/:/app/libs/* "com.dmakarov.BillingApplication"  "$@"
