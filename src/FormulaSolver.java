import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FormulaSolver {

    public static long solve(String formulaString) {
        return new Formula(new FormulaTokenizer(formulaString)).solve();
    }

    private static class Formula {

        final FormulaNode root;

        Formula(FormulaTokenizer tokenizer) {
            root = FormulaNode.create(tokenizer);
        }

        long solve() {
            return root.solve();
        }
    }

    private interface FormulaNode {

        long solve();

        static FormulaNode create(FormulaTokenizer tokenizer) {
            List<FormulaNode> nodes = new ArrayList<>();
            List<Operator> operations = new ArrayList<>();
            while(true) {
                Token token = tokenizer.getNextToken();
                if (token instanceof Brace) {
                    if (((Brace) token).isOpen()) {
                        addNode(nodes, operations, FormulaNode.create(tokenizer));
                    } else {
                        // end brace
                        break;
                    }
                } else if (token instanceof Operator) {
                    addOperator(nodes, operations, (Operator) token);
                } else if (token instanceof NumberToken) {
                    addNode(nodes, operations,
                            new NumberFormulaNode(((NumberToken) token).getNumber()));
                } else {
                    // null token (end of formula)
                    break;
                }
            }
            if (operations.size() + 1 != nodes.size()) {
                throw new IllegalArgumentException("There is operator in the end of formula");
            }
            for (int i = 1; i <= OperatorFactory.MAX_OPERATOR_PRIORITY; i++) {
                for (int j = 0; j < operations.size(); j++) {
                    if (operations.get(j).getPriority() == i) {
                        FormulaNode newNode = new OperatorFormulaNode(operations.get(j),
                                nodes.get(j), nodes.get(j + 1));
                        operations.set(j, null);
                        nodes.set(j, null);
                        nodes.set(j + 1, newNode);
                    }
                }
                List<FormulaNode> newNodes = new ArrayList<>();
                List<Operator> newOperations = new ArrayList<>();
                for (int j = 0; j < operations.size(); j++) {
                    assert (nodes.get(j) == null) == (operations.get(j) == null);
                    if (nodes.get(j) != null) {
                        newNodes.add(nodes.get(j));
                        newOperations.add(operations.get(j));
                    }
                }
                newNodes.add(nodes.get(nodes.size() - 1));
                nodes = newNodes;
                operations = newOperations;
            }
            assert nodes.size() == 1;
            return nodes.get(0);
        }

        static void addNode(List<FormulaNode> nodes,
                            List<Operator> operations,
                            FormulaNode node) {
            nodes.add(node);
            if (operations.size() + 1 != nodes.size()) {
                throw new IllegalArgumentException("There is two numbers in a row");
            }
        }

        static void addOperator(List<FormulaNode> nodes,
                                List<Operator> operations,
                                Operator operator) {
            operations.add(operator);
            if (operations.size() != nodes.size()) {
                throw new IllegalArgumentException("There is two operators in a row");
            }
        }
    }

    private static class NumberFormulaNode implements FormulaNode {

        long number;

        NumberFormulaNode(long number) {
            this.number = number;
        }

        @Override
        public long solve() {
            return number;
        }
    }

    private static class OperatorFormulaNode implements FormulaNode {

        Operator operator;
        FormulaNode node1;
        FormulaNode node2;

        OperatorFormulaNode(Operator operator, FormulaNode node1, FormulaNode node2) {
            this.operator = operator;
            this.node1 = node1;
            this.node2 = node2;
        }

        @Override
        public long solve() {
            return operator.getValue(node1.solve(), node2.solve());
        }
    }

    private static class FormulaTokenizer {

        final String formulaString;
        int index;

        FormulaTokenizer(String formulaString) {
            this.formulaString = formulaString;
        }

        Token getNextToken() {
            if (index >= formulaString.length()) {
                return null;
            }
            while(formulaString.charAt(index) == ' ') {
                index++;
            }
            char c = formulaString.charAt(index);
            if (c == '(' || c == ')' ) {
                index++;
                return new Brace(c == '(');
            }
            if (isDigit(c)) {
                int endIndex = index;
                while (endIndex < formulaString.length() && isDigit(formulaString.charAt(endIndex))) {
                    endIndex++;
                }
                int startIndex = index;
                index = endIndex;
                return new NumberToken(Long.parseLong(formulaString.substring(startIndex, endIndex)));
            }
            if (OperatorFactory.isOperatorSymbol(c)) {
                int endIndex = index;
                while (endIndex < formulaString.length() &&
                        OperatorFactory.isOperatorSymbol(formulaString.charAt(endIndex))) {
                    endIndex++;
                }
                int startIndex = index;
                index = endIndex;
                return OperatorFactory.getOperator(formulaString.substring(startIndex, endIndex));
            }
            throw new IllegalArgumentException("Illegal token: " +
                    formulaString.substring(index));
        }

        static boolean isDigit(char c) {
            return c >= '0' && c <= '9';
        }
    }

    private static interface Token {
    }

    private static interface Operator extends Token {
        long getValue(long value1, long value2);
        int getPriority();
    }

    private static class OperatorFactory {

        static final int MAX_OPERATOR_PRIORITY = 2;
        static final String OPERATOR_SYMBOLS = "+*-/";

        static Map<String, Operator> operators = new HashMap<>();
        static Set<Character> operatorSymbols = new HashSet<>();

        static {
            for (int i = 0; i < OPERATOR_SYMBOLS.length(); i++) {
                operatorSymbols.add(OPERATOR_SYMBOLS.charAt(i));
            }
            operators.put("+", new Operator() {
                @Override
                public long getValue(long value1, long value2) {
                    return value1 + value2;
                }

                @Override
                public int getPriority() {
                    return 2;
                }

                @Override
                public String toString() {
                    return "+";
                }
            });
            operators.put("-", new Operator() {
                @Override
                public long getValue(long value1, long value2) {
                    return value1 - value2;
                }

                @Override
                public int getPriority() {
                    return 2;
                }

                @Override
                public String toString() {
                    return "-";
                }
            });
            operators.put("*", new Operator() {
                @Override
                public long getValue(long value1, long value2) {
                    return value1 * value2;
                }

                @Override
                public int getPriority() {
                    return 1;
                }

                @Override
                public String toString() {
                    return "*";
                }
            });
            operators.put("/", new Operator() {
                @Override
                public long getValue(long value1, long value2) {
                    return value1 / value2;
                }

                @Override
                public int getPriority() {
                    return 1;
                }

                @Override
                public String toString() {
                    return "/";
                }
            });
        }

        static Operator getOperator(String operatorString) {
            return operators.get(operatorString);
        }

        static boolean isOperatorSymbol(char c) {
            return operatorSymbols.contains(c);
        }
    }

    private static class Brace implements Token {

        boolean isOpen;

        Brace(boolean isOpen) {
            this.isOpen = isOpen;
        }

        boolean isOpen() {
            return isOpen;
        }

        @Override
        public String toString() {
            if (isOpen) {
                return "(";
            } else {
                return ")";
            }
        }
    }

    private static class NumberToken implements Token {

        long number;

        NumberToken(long number) {
            this.number = number;
        }

        long getNumber() {
            return number;
        }

        @Override
        public String toString() {
            return Long.toString(number);
        }
    }
}
