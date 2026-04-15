package com.doharu.binzip_recommend.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CsvService {

    public List<BackData> readCsv() {
        try {
            InputStream is = getClass()
                    .getClassLoader()
                    .getResourceAsStream("binzip_data.csv");

            if (is == null) {
                throw new RuntimeException("CSV 파일 못 찾음");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            boolean isFirst = true;

            List<BackData> list = new ArrayList<>();

            while ((line = reader.readLine()) != null) {

                if (isFirst) {
                    isFirst = false;
                    continue;
                }

                // 🔥 핵심 수정
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                String regionName = parts[1];
                String regionDetail = parts[2];

                double latitude = Double.parseDouble(parts[9]);
                double longitude = Double.parseDouble(parts[10]);

                int facilityCount = Integer.parseInt(parts[11]);
                double crowd = Double.parseDouble(parts[12]);

                double age20 = Double.parseDouble(parts[13]);
                double age30 = Double.parseDouble(parts[14]);
                double age40 = Double.parseDouble(parts[15]);
                double age50 = Double.parseDouble(parts[16]);
                double age60 = Double.parseDouble(parts[17]);
                double ageEtc = Double.parseDouble(parts[18]);

                // 🔥 따옴표 제거
                String targetRaw = parts[19].replace("\"", "");

                List<Integer> targetAges = Arrays.stream(targetRaw.split(","))
                        .map(String::trim)
                        .map(Integer::parseInt)
                        .toList();

                int price = Integer.parseInt(parts[20]);

                BackData data = new BackData(
                        latitude,
                        longitude,
                        facilityCount,
                        crowd,
                        targetAges,
                        age20,
                        age30,
                        age40,
                        age50,
                        age60,
                        ageEtc,
                        price
                );
                list.add(data);

            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
