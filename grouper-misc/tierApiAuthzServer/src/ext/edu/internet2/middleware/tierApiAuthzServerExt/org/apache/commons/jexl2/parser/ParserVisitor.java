/* Generated By:JavaCC: Do not edit this line. ParserVisitor.java Version 5.0 */
package edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser;

import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTAdditiveNode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTAdditiveOperator;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTAmbiguous;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTAndNode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTArrayAccess;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTArrayLiteral;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTAssignment;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTBitwiseAndNode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTBitwiseComplNode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTBitwiseOrNode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTBitwiseXorNode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTBlock;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTConstructorNode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTDivNode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTEQNode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTERNode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTEmptyFunction;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTFalseNode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTFloatLiteral;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTForeachStatement;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTFunctionNode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTGENode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTGTNode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTIdentifier;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTIfStatement;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTIntegerLiteral;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTJexlScript;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTLENode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTLTNode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTMapEntry;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTMapLiteral;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTMethodNode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTModNode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTMulNode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTNENode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTNRNode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTNotNode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTNullLiteral;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTOrNode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTReference;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTSizeFunction;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTSizeMethod;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTStringLiteral;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTTernaryNode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTTrueNode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTUnaryMinusNode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ASTWhileStatement;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.SimpleNode;

public interface ParserVisitor
{
  public Object visit(SimpleNode node, Object data);
  public Object visit(ASTJexlScript node, Object data);
  public Object visit(ASTBlock node, Object data);
  public Object visit(ASTAmbiguous node, Object data);
  public Object visit(ASTIfStatement node, Object data);
  public Object visit(ASTWhileStatement node, Object data);
  public Object visit(ASTForeachStatement node, Object data);
  public Object visit(ASTAssignment node, Object data);
  public Object visit(ASTTernaryNode node, Object data);
  public Object visit(ASTOrNode node, Object data);
  public Object visit(ASTAndNode node, Object data);
  public Object visit(ASTBitwiseOrNode node, Object data);
  public Object visit(ASTBitwiseXorNode node, Object data);
  public Object visit(ASTBitwiseAndNode node, Object data);
  public Object visit(ASTEQNode node, Object data);
  public Object visit(ASTNENode node, Object data);
  public Object visit(ASTLTNode node, Object data);
  public Object visit(ASTGTNode node, Object data);
  public Object visit(ASTLENode node, Object data);
  public Object visit(ASTGENode node, Object data);
  public Object visit(ASTERNode node, Object data);
  public Object visit(ASTNRNode node, Object data);
  public Object visit(ASTAdditiveNode node, Object data);
  public Object visit(ASTAdditiveOperator node, Object data);
  public Object visit(ASTMulNode node, Object data);
  public Object visit(ASTDivNode node, Object data);
  public Object visit(ASTModNode node, Object data);
  public Object visit(ASTUnaryMinusNode node, Object data);
  public Object visit(ASTBitwiseComplNode node, Object data);
  public Object visit(ASTNotNode node, Object data);
  public Object visit(ASTIdentifier node, Object data);
  public Object visit(ASTNullLiteral node, Object data);
  public Object visit(ASTTrueNode node, Object data);
  public Object visit(ASTFalseNode node, Object data);
  public Object visit(ASTIntegerLiteral node, Object data);
  public Object visit(ASTFloatLiteral node, Object data);
  public Object visit(ASTStringLiteral node, Object data);
  public Object visit(ASTArrayLiteral node, Object data);
  public Object visit(ASTMapLiteral node, Object data);
  public Object visit(ASTMapEntry node, Object data);
  public Object visit(ASTEmptyFunction node, Object data);
  public Object visit(ASTSizeFunction node, Object data);
  public Object visit(ASTFunctionNode node, Object data);
  public Object visit(ASTMethodNode node, Object data);
  public Object visit(ASTSizeMethod node, Object data);
  public Object visit(ASTConstructorNode node, Object data);
  public Object visit(ASTArrayAccess node, Object data);
  public Object visit(ASTReference node, Object data);
}
/* JavaCC - OriginalChecksum=2da35afa98d58012c57568ab3338307f (do not edit this line) */
