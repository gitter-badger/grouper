/* Generated By:JJTree: Do not edit this line. ASTBitwiseComplNode.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser;

import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.JexlNode;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.Parser;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.jexl2.parser.ParserVisitor;

public
class ASTBitwiseComplNode extends JexlNode {
  public ASTBitwiseComplNode(int id) {
    super(id);
  }

  public ASTBitwiseComplNode(Parser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=fdf26ed5f9ee99f1b5fccc909084f934 (do not edit this line) */
