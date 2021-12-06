package ru.itis.kpfu.simplesystem.neuroClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.*;
import java.util.List;

import static ru.itis.kpfu.simplesystem.neuroClient.TestClient.publicKey16;

public class NeuralNetwork {

    private double w11, w12, w21, w22, v11, v12, v13, v21, v22, v23, w1, w2, w3;
    private double e;

    private static double x1[] = new double[100];
    private static double x2[] = new double[100];
    private static double y[] = new double[100];

    static {
        readTestData100();
    }

    public NeuralNetwork(double w11, double w12, double w21, double w22, double v11, double v12, double v13, double v21, double v22, double v23, double w1, double w2, double w3) {
        this.w11 = w11;
        this.w12 = w12;
        this.w21 = w21;
        this.w22 = w22;
        this.v11 = v11;
        this.v12 = v12;
        this.v13 = v13;
        this.v21 = v21;
        this.v22 = v22;
        this.v23 = v23;
        this.w1 = w1;
        this.w2 = w2;
        this.w3 = w3;
    }

    public NeuralNetwork(DataModel model) throws NumberFormatException {
        init(model);
    }

    public NeuralNetwork() {
    }

    public void init(DataModel model) throws NumberFormatException {
        this.w11 = Double.parseDouble(model.getW11());
        this.w12 = Double.parseDouble(model.getW12());
        this.w21 = Double.parseDouble(model.getW21());
        this.w22 = Double.parseDouble(model.getW22());
        this.v11 = Double.parseDouble(model.getV11());
        this.v12 = Double.parseDouble(model.getV12());
        this.v13 = Double.parseDouble(model.getV13());
        this.v21 = Double.parseDouble(model.getV21());
        this.v22 = Double.parseDouble(model.getV22());
        this.v23 = Double.parseDouble(model.getV23());
        this.w1 = Double.parseDouble(model.getW1());
        this.w2 = Double.parseDouble(model.getW2());
        this.w3 = Double.parseDouble(model.getW3());
    }

    private double f(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    public double g(double x1, double x2) {
        double h11 = f(x1 * w11 + x2 * w21);
        double h12 = f(x1 * w12 + x2 * w22);
        return f(f(h11 * v11 + h12 * v21) * w1 + f(h11 * v12 + h12 * v22) * w2 + f(h11 * v13 + h12 * v23));
    }

    public double e() {
        double res = 0;
        for (int i = 0; i < 100; i++) {
            double yt = g(x1[i], x2[i]);
            res += (yt - y[i]) * (yt - y[i]);
        }
        return res;
    }

    private static void readTestData100() {
        List<String> lstData;
        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader(new File("test_data_100.csv")));
            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                String[] s = line.split(";");
                x1[i] = Double.parseDouble(s[0]);
                x2[i] = Double.parseDouble(s[1]);
                y[i] = Double.parseDouble(s[2]);
                //System.out.println(x1[i] +";" + x2[i] +";" + y[i]);
                i++;
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (NumberFormatException exception) {
            exception.printStackTrace();
        }
    }

    @SneakyThrows
    public static void main(String[] args) {
        ObjectMapper mapper = new ObjectMapper();
        DataModel dm = mapper.readValue("{\"w11\":\"0.40450895\",\"w12\":\"0.6409668\",\"w21\":\"-0.0528092\",\"w22\":\"0.69250953\",\n" +
                "\"v11\":\"-0.748313\",\"v12\":\"0.56010175\",\"v13\":\"0.891308\",\"v21\":\"0.93825704\",\"v22\":\"-0.34686327\",\"v23\":\"-0.15610261\",\n" +
                "\"w1\":\"0.20637804\",\"w2\":\"-0.78078914\",\"w3\":\"1.3415401 \"\n" +
                ",\"e\":\"0.24078458186488258\" ,\"publickey\":\"" + publicKey16 + "\"}", DataModel.class);
//        DataModel dm = new DataModel("1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1","","");
        System.out.println(dm.toString());
        NeuralNetwork nn = new NeuralNetwork(dm);
        System.out.println(nn.e());

    }
//    Layer #0: [[ 0.40450895 0.6409668 ]
//            [-0.0528092 0.69250953]]
//    Layer #1: [[-0.748313 0.56010175 0.891308 ]
//            [ 0.93825704 -0.34686327 -0.15610261]]
//    Layer #2: [[ 0.20637804]
//            [-0.78078914]
//            [ 1.3415401 ]]
}