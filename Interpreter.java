import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Interpreter {

    private final List<Instruction> code;
    private final Deque<Double> stack = new ArrayDeque<>();
    private final Map<String, Double> vars = new HashMap<>();
    private final Scanner scanner = new Scanner(System.in);

    public Interpreter(List<Instruction> code) {
        this.code = code;
    }

    public void run() {
        int pc = 0; // program counter

        while (pc < code.size()) {
            Instruction ins = code.get(pc);

            switch (ins.op) {
                case PUSH_CONST:
                    stack.push(ins.numOperand);
                    pc++;
                    break;
                case LOAD_VAR:
                    stack.push(vars.getOrDefault(ins.strOperand, 0.0));
                    pc++;
                    break;
                case STORE_VAR: {
                    double value = stack.pop();
                    vars.put(ins.strOperand, value);
                    pc++;
                    break;
                }
                case ADD: {
                    double b = stack.pop();
                    double a = stack.pop();
                    stack.push(a + b);
                    pc++;
                    break;
                }
                case SUB: {
                    double b = stack.pop();
                    double a = stack.pop();
                    stack.push(a - b);
                    pc++;
                    break;
                }
                case MUL: {
                    double b = stack.pop();
                    double a = stack.pop();
                    stack.push(a * b);
                    pc++;
                    break;
                }
                case DIV: {
                    double b = stack.pop();
                    double a = stack.pop();
                    stack.push(a / b);
                    pc++;
                    break;
                }
                case CMP_EQ: {
                    double b = stack.pop();
                    double a = stack.pop();
                    stack.push(a == b ? 1.0 : 0.0);
                    pc++;
                    break;
                }
                case CMP_NEQ: {
                    double b = stack.pop();
                    double a = stack.pop();
                    stack.push(a != b ? 1.0 : 0.0);
                    pc++;
                    break;
                }
                case CMP_GT: {
                    double b = stack.pop();
                    double a = stack.pop();
                    stack.push(a > b ? 1.0 : 0.0);
                    pc++;
                    break;
                }
                case CMP_GTE: {
                    double b = stack.pop();
                    double a = stack.pop();
                    stack.push(a >= b ? 1.0 : 0.0);
                    pc++;
                    break;
                }
                case CMP_LT: {
                    double b = stack.pop();
                    double a = stack.pop();
                    stack.push(a < b ? 1.0 : 0.0);
                    pc++;
                    break;
                }
                case CMP_LTE: {
                    double b = stack.pop();
                    double a = stack.pop();
                    stack.push(a <= b ? 1.0 : 0.0);
                    pc++;
                    break;
                }
                case PRINT: {
                    double value = stack.pop();
                    System.out.println(value);
                    pc++;
                    break;
                }
                case INPUT: {
                    System.out.print(ins.strOperand + " = ");
                    double value = scanner.nextDouble();
                    vars.put(ins.strOperand, value);
                    pc++;
                    break;
                }
                case JMP:
                    pc = ins.jumpTarget;
                    break;
                case JMP_IF_FALSE: {
                    double cond = stack.pop();
                    if (cond == 0.0) {
                        pc = ins.jumpTarget;
                    } else {
                        pc++;
                    }
                    break;
                }
                case POP:
                    stack.pop();
                    pc++;
                    break;
                default:
                    throw new RuntimeException("Opcode desconhecido: " + ins.op);
            }
        }
    }
}
