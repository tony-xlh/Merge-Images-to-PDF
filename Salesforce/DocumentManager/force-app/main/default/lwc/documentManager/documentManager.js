import { LightningElement } from 'lwc';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import { loadScript, loadStyle } from 'lightning/platformResourceLoader';
import ddv from '@salesforce/resourceUrl/ddv';

export default class DocumentManager extends LightningElement {
  ddvInitialized = false;
  ddvFrame;
  
  renderedCallback() {
      if (this.ddvInitialized) {
          return;
      }
      this.ddvInitialized = true;

      this.ddvFrame = document.createElement('iframe');
      this.ddvFrame.src = ddv + "/index.html";
      // div tag in which iframe will be added should have id attribute with value myDIV
      this.template.querySelector("div.ddvViewer").appendChild(this.ddvFrame);

      // provide height and width to it
      this.ddvFrame.setAttribute("style","height:100%;width:100%;");
  }
}