public class Instruction {
    public final OpCode op;
    public final String strOperand; // para variáveis
    public final double numOperand; // para constantes
    public int jumpTarget;          // para saltos (pode ser modificado na geração)

    // Instrução sem operandos
    public Instruction(OpCode op) {
        this(op, null, 0.0, -1);
    }

    // Instrução com número
    public Instruction(OpCode op, double numOperand) {
        this(op, null, numOperand, -1);
    }

    // Instrução com string
    public Instruction(OpCode op, String strOperand) {
        this(op, strOperand, 0.0, -1);
    }

    // Instrução de salto
    public Instruction(OpCode op, int jumpTarget) {
        this(op, null, 0.0, jumpTarget);
    }

    public Instruction(OpCode op, String strOperand, double numOperand, int jumpTarget) {
        this.op = op;
        this.strOperand = strOperand;
        this.numOperand = numOperand;
        this.jumpTarget = jumpTarget;
    }

    @Override
    public String toString() {
        switch (op) {
            case PUSH_CONST: return op + " " + numOperand;
            case LOAD_VAR:
            case STORE_VAR:
            case INPUT:     return op + " " + strOperand;
            case JMP:
            case JMP_IF_FALSE: return op + " -> " + jumpTarget;
            default: return op.toString();
        }
    }
}
