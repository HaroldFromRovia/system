package ru.itis.kpfu.stats;

import lombok.SneakyThrows;
import org.apache.commons.math3.stat.StatUtils;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.DoubleUnaryOperator;

public class Main {

    public static DateFormat fileDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @SneakyThrows
    public static void main(String[] args) {
        DateFormat betweenFormat = new SimpleDateFormat("yyyy-MM-dd");

        evaluate(betweenFormat.parse("2020-09-01"), betweenFormat.parse("2020-09-04"),
                Calendar.HOUR, x -> x);
        evaluate(betweenFormat.parse("2020-09-01"), betweenFormat.parse("2020-10-01"),
                Calendar.DAY_OF_MONTH, x -> x / 24);
        evaluate(betweenFormat.parse("2020-08-03"), betweenFormat.parse("2020-11-01"),
                Calendar.WEEK_OF_YEAR, x -> x / (24 * 7));
        evaluate(betweenFormat.parse("2020-04-01"), betweenFormat.parse("2021-11-01"),
                Calendar.MONTH, x -> x / (24 * 30));
    }

    public static void evaluate(Date lowerBound, Date upperBound, int interval, DoubleUnaryOperator mapper) {
        List<Double> resultList = new ArrayList<>();
        int count = 0;
        resultList.add(0.0);
        Calendar calendar = Calendar.getInstance();
        Calendar currentCalendar = Calendar.getInstance();
        calendar.setTime(lowerBound);
        currentCalendar.setTime(lowerBound);
        currentCalendar.add(interval, 1);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("ddd.csv")))) {
            String dateString;

            while ((dateString = reader.readLine()) != null) {
                Date date = fileDateFormat.parse(dateString);
                if (date.before(upperBound) && date.after(lowerBound)) {

                    if (date.before(currentCalendar.getTime()) && date.after(calendar.getTime())) {
                        resultList.set(count, resultList.get(count) + 1);
                    } else {
                        count++;
                        calendar.add(interval, 1);
                        currentCalendar.add(interval, 1);
                        resultList.add(0.0);
                    }
                }

                if (date.after(upperBound)) break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        printStats(resultList, mapper);
    }

    public static void printStats(List<Double> values, DoubleUnaryOperator mapper) {
        double[] resultArray = values.stream()
                .mapToDouble(Double::doubleValue)
                .map(mapper)
                .toArray();

        for (int i = 0; i < resultArray.length; i++) {
            System.out.printf("Event %d | Frequency = %.2f %n", i + 1, resultArray[i]);
        }

        double mean = StatUtils.mean(resultArray);
        double variance = StatUtils.variance(resultArray);

        System.out.printf("Mean = %.2f %n", mean);
        System.out.printf("Deviation = %.2f %n", Math.sqrt(variance));
    }
}
