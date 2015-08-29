public class Main {

    public static void main(String[] args) {
        assert FormulaSolver.solve("(4 + 5) * 6 / 3") == 18;
        assert FormulaSolver.solve("4 + 5 * 6 / 3") == 14;
        assert FormulaSolver.solve("5") == 5;
        assert FormulaSolver.solve("0") == 0;
        assert FormulaSolver.solve("123456789") == 123456789;
        assert FormulaSolver.solve("(555)") == 555;
        assert FormulaSolver.solve("((555))") == 555;
        assert FormulaSolver.solve("(((555)))") == 555;
        assert FormulaSolver.solve("1 + 1") == 2;
        assert FormulaSolver.solve("1 - 1") == 0;
        assert FormulaSolver.solve("1 - 1000") == -999;
        assert FormulaSolver.solve("100-100") == 0;
        assert FormulaSolver.solve("10*7") == 70;
        assert FormulaSolver.solve("0*7") == 0;
        assert FormulaSolver.solve("7*0") == 0;
        assert FormulaSolver.solve("7/7") == 1;
        assert FormulaSolver.solve("77/7") == 11;
        assert FormulaSolver.solve("77/1") == 77;
        assert FormulaSolver.solve("((55 - 53)   /(33/3 )) * (1 + 3 + 0 - 2)") == 4;
        assert FormulaSolver.solve("6 + 5 + 4 + 3 + 2 + 5/5") == 21;
        assert FormulaSolver.solve("2*3 + 5 + 4 + 3 + 2 + 5/5") == 21;
        assert FormulaSolver.solve("2*3 + 5 + 4 + 9/3 + 2 + 5/5") == 21;
    }
}